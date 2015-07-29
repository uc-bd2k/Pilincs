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


    @RequestMapping(value = "/api-assays-paged", method = RequestMethod.GET)
    public
    @ResponseBody
    TableResponse tableAsJsonPaged(
            @RequestParam String order,
            @RequestParam Integer limit,
            @RequestParam Integer offset,
            @RequestParam String tags) throws ParseException {

        List<AssayRecord> output = new ArrayList<>();

        HashMap<String,List<String>> tagsParsed = Utils.parseTags(tags);

        PageRequest pageRequest = new PageRequest(offset / limit, limit);
        Page<PeakArea> result;
        Long count;

        List<String> allTagsForPertiname = new ArrayList<>();
        List<String> allTagsForcell = new ArrayList<>();
        List<String> allTagsForgenesymbol = new ArrayList<>();

        for (TagFormat tagFormat : getTagsForAutocompletion()) {
            String annotation = tagFormat.getAnnotation();
            switch (annotation) {
                case "Pertiname":
                    allTagsForPertiname.add(tagFormat.getName());
                    break;
                case "CellId":
                    allTagsForcell.add(tagFormat.getName());
                    break;
                case "PrGeneSymbol":
                    allTagsForgenesymbol.add(tagFormat.getName());
                    break;
            }
        }

        List<String> assayTypesString = tagsParsed.get("AssayTypes");
        List<AssayType> assayTypes = new ArrayList<>();

        for(String string : assayTypesString){
            assayTypes.add(AssayType.valueOf(string));
        }

        List<String> pertinameTags = tagsParsed.get("Pertiname").size() > 0 ? tagsParsed.get("Pertiname") : allTagsForPertiname;
        List<String> cellTags = tagsParsed.get("CellId").size() > 0 ? tagsParsed.get("CellId") : allTagsForcell;
        List<String> genesymbolTags = tagsParsed.get("PrGeneSymbol").size() > 0 ? tagsParsed.get("PrGeneSymbol") : allTagsForgenesymbol;

        result = peakAreaRepository.findByGctFileAssayTypeInAndReplicateAnnotationPertinameInAndReplicateAnnotationCellIdInAndPeptideAnnotationPrGeneSymbolIn(
                assayTypes,
                pertinameTags,
                cellTags,
                genesymbolTags,
                pageRequest);

            count = result.getTotalElements();

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
            output.add(new Tuples.Tuple2<>(fileName,urlString));
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

           TagFormat tagFormat = new TagFormat(peptideAnnotation.getPrGeneSymbol(),"Peptide","PrGeneSymbol");
            if(tagFormat.getName() != null && !output.contains(tagFormat))output.add(tagFormat);
        }
        return output;
    }

    @RequestMapping(value = "/api-recommend", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    List<TagFormat> getTagsFromRecommenderPost(@RequestBody String tags) throws ParseException {

        List<TagFormat> output = new ArrayList<>();

        HashMap<String,List<String>> tagsParsed = Utils.parseTags(tags);
        String lastTagAnnotation = Utils.lastTag(tags);
        if(lastTagAnnotation == null)return output;

        List<String> allTagsForAnnotation = new ArrayList<>();

        for (TagFormat tagFormat : getTagsForAutocompletion()) {

            String annotation = tagFormat.getAnnotation();

            if(annotation.equals(lastTagAnnotation) && !tagsParsed.get(annotation).contains(tagFormat.getName())){
                allTagsForAnnotation.add(tagFormat.getName());
                output.add(tagFormat);
                if(allTagsForAnnotation.size() >= 8) break;
            }
        }
        return output;
    }
}
