package edu.uc.eh.controller;

import edu.uc.eh.domain.*;
import edu.uc.eh.domain.json.*;
import edu.uc.eh.domain.repository.GctFileRepository;
import edu.uc.eh.domain.repository.PeakAreaRepository;
import edu.uc.eh.domain.repository.PeptideAnnotationRepository;
import edu.uc.eh.domain.repository.ReplicateAnnotationRepository;
import edu.uc.eh.service.ConnectPanorama;
import edu.uc.eh.service.QueryService;
import edu.uc.eh.service.Tuples;
import edu.uc.eh.service.Utils;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by chojnasm on 7/15/15.
 */

@Controller
public class RestController {


    private final ConnectPanorama connectPanorama;
    private final GctFileRepository gctFileRepository;
    private final ReplicateAnnotationRepository replicateAnnotationRepository;
    private final PeptideAnnotationRepository peptideAnnotationRepository;
    private final PeakAreaRepository peakAreaRepository;
    private final QueryService queryService;


    @Autowired

    public RestController(ConnectPanorama connectPanorama,
                          GctFileRepository gctFileRepository,
                          ReplicateAnnotationRepository replicateAnnotationRepository,
                          PeptideAnnotationRepository peptideAnnotationRepository,
                          PeakAreaRepository peakAreaRepository,
                          QueryService queryService) {
        this.connectPanorama = connectPanorama;
        this.gctFileRepository = gctFileRepository;
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
            System.out.println("Parsed: "+s);
        }

        PageRequest pageRequest = new PageRequest(offset / limit, limit);
        Page<PeakArea> result;
        Long count;

        if(tagsParsed.size()==0){
            result = peakAreaRepository.findAll(pageRequest);
            count = peakAreaRepository.count();
        }else{

            List<String> tagsNotParsed = new ArrayList<>();

            List<String> allTags = new ArrayList<>();

            for(TagFormat tagFormat : getTagsForAutocompletion()){
                allTags.add(tagFormat.getName());
                if(!tagsParsed.contains(tagFormat.getName()))
                    tagsNotParsed.add(tagFormat.getName());
            }



//            result = peakAreaRepository.findByReplicateAnnotationPertinameIn(tagsParsed, pageRequest);
//            count = peakAreaRepository.countByReplicateAnnotationPertinameIn(tagsParsed);
            result = peakAreaRepository.findByReplicateAnnotationPertinameInAndReplicateAnnotationCellIdIn(
                    tagsParsed,allTags,pageRequest);
            count = result.getTotalElements();
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
    List<Tuples.Tuple2<String,String>> gctUrlsFromPanorama() throws Exception {
        List<Tuples.Tuple2<String,String>> output = new ArrayList<>();

        for(String urlString : connectPanorama.GctUrls()){
            URL url = new URL(urlString);
            String fileName = null;
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String raw = conn.getHeaderField("Content-Disposition");
            if(raw != null && raw.indexOf("=") != -1) {
                fileName = raw.split("=")[1].replaceAll("\"","");
            }
            output.add(new Tuples.Tuple2<String, String>(fileName,urlString));
        }
        return output;
    }


    @RequestMapping(value = "/api-tags",method = RequestMethod.GET)
    public
    @ResponseBody
    List<TagFormat> getTagsForAutocompletion(){

        List<TagFormat> output = new ArrayList<>();

        Iterator<ReplicateAnnotation> allReplicates = replicateAnnotationRepository.findAll().iterator();
        Iterator<PeptideAnnotation> allPeptides = peptideAnnotationRepository.findAll().iterator();

        while(allReplicates.hasNext()){
            ReplicateAnnotation replicateAnnotation = allReplicates.next();

            TagFormat tagFormat = new TagFormat(replicateAnnotation.getPertiname(),"Replicate","Pertiname");
            if(tagFormat.getName() != null && !output.contains(tagFormat))output.add(tagFormat);

            tagFormat = new TagFormat(replicateAnnotation.getCellId(),"Cell","CellId");
            if(tagFormat.getName() != null && !output.contains(tagFormat))output.add(tagFormat);
        }

        while(allPeptides.hasNext()){
            PeptideAnnotation peptideAnnotation = allPeptides.next();

            TagFormat tagFormat = new TagFormat(peptideAnnotation.getPrCluster(),"Peptide","PrCluster");
            if(tagFormat.getName() != null && !output.contains(tagFormat))output.add(tagFormat);

            tagFormat = new TagFormat(peptideAnnotation.getPrGeneSymbol(),"Peptide","PrGeneSymbol");
            if(tagFormat.getName() != null && !output.contains(tagFormat))output.add(tagFormat);
        }
        return output;
    }

    @RequestMapping(value = "/api-recommend",method = RequestMethod.GET)
    public
    @ResponseBody
    List<TagFormat> getTagsFromRecommender(){

        List<TagFormat> output = new ArrayList<>();
        output.add(new TagFormat("MS-275","Replicate","Pertiname"));
        return output;
    }

    @RequestMapping(value = "/api-recommend", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    List<TagFormat> getTagsFromRecommenderPost(@RequestBody String tags) {
        System.out.println("Tags: "+tags.toString());
        List<TagFormat> output = new ArrayList<>();
        output.add(new TagFormat("MS-275","Replicate","Pertiname"));
        output.add(new TagFormat("PC3","Cell","CellId"));
        return output;
    }
}
