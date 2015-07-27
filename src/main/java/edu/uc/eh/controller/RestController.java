package edu.uc.eh.controller;

import edu.uc.eh.domain.*;
import edu.uc.eh.domain.json.*;
import edu.uc.eh.domain.repository.PeakAreaRepository;
import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import edu.uc.eh.domain.repository.ReplicateAnnotationRepository;
import edu.uc.eh.service.ConnectPanorama;
import edu.uc.eh.service.QueryService;
import edu.uc.eh.service.Utils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

/**
 * Created by chojnasm on 7/15/15.
 */

@Controller
public class RestController {


    private final ConnectPanorama connectPanorama;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final QueryService queryService;


    @Autowired
    public RestController(
            ConnectPanorama connectPanorama,
            ReplicateAnnotationRepository replicateAnnotationRepository,
            PeptideAnnotationRepository peptideAnnotationRepository,
            PeakAreaRepository peakAreaRepository,
            QueryService queryService) {
        this.connectPanorama = connectPanorama;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
        this.peptideAnnotationRepository = peptideAnnotationRepository;
        this.peakAreaRepository = peakAreaRepository;
        this.queryService = queryService;
    }


    @RequestMapping(value = "/api-assays", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    List<AssayRecord> tableAsJson(@RequestBody List<Query> tags) {
        return queryService.getAllAssays(tags);
    }


    @RequestMapping(value = "/api-assays-paged",
            method = RequestMethod.GET)
    public
    @ResponseBody
    TableResponse tableAsJsonPaged(
            @RequestParam String order,
            @RequestParam Integer limit,
            @RequestParam Integer offset,
            @RequestParam String tags) throws ParseException {

        List<AssayRecord> output = new ArrayList<>();
        Collection<String> tagsParsed = Utils.parseTags(tags);
        for(String s: tagsParsed){
            System.out.println(s);
        }

        PageRequest pageRequest = new PageRequest(offset / limit, limit);
        Page<PeakArea> result = null;
        Long count = null;

        if(tagsParsed.size()==0){
            result = peakAreaRepository.findAll(pageRequest);
            count = peakAreaRepository.count();
        }else{
            result = peakAreaRepository.findByReplicateAnnotationPertinameIn(tagsParsed, pageRequest);
            count = peakAreaRepository.countByReplicateAnnotationPertinameIn(tagsParsed);
        }


        System.out.println(order+" "+limit+" "+offset+ " "+tagsParsed);

        for(PeakArea peakArea : result){
            output.add(new AssayRecord(peakArea));
        }

        return new TableResponse(count,output);
    }


    @RequestMapping(value = "/api-panorama", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> gctUrlsFromPanorama() throws Exception {
        return connectPanorama.GctUrls();
    }


    @RequestMapping(value = "/api-tags",method = RequestMethod.GET)
    public
    @ResponseBody
    List<TagFormat> getTagsForAutocompletion(){

        List<TagFormat> output = new ArrayList<>();

        Iterator<ReplicateAnnotation> allReplicates = replicateAnnotationRepository.findAll().iterator();
        Iterator<PeptideAnnotation> allPeptides = peptideAnnotationRepository.findAll().iterator();

        while(allReplicates.hasNext()){
            TagFormat tagFormat = new TagFormat(allReplicates.next().getPertiname(),"Peptide","pertIname",21);
            if(!output.contains(tagFormat))output.add(tagFormat);
        }
        while(allPeptides.hasNext()){
            TagFormat tagFormat = new TagFormat(allPeptides.next().getPrCluster(),"Replicate","prCluster",11);
            if(!output.contains(tagFormat))output.add(tagFormat);
        }
        return output;
    }

    @RequestMapping(value = "/api-recommend",method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> getTagsFromRecommender(){
        List<ReplicateAnnotation> all = replicateAnnotationRepository.findAll();
        List<String> output = new ArrayList<>();
        for(ReplicateAnnotation ra : all){
            String label = ra.getPertiname();
            if(!output.contains(label))output.add(label);
        }

        if(output.size()<5){
            return output;
        }else{
            return output.subList(0,5);
        }

    }
}
