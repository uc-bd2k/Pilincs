package edu.uc.eh.controller;

import edu.uc.eh.domain.AssayRecord;
import edu.uc.eh.domain.Query;
import edu.uc.eh.domain.ReplicateAnnotation;
import edu.uc.eh.domain.repository.ReplicateAnnotationRepository;
import edu.uc.eh.service.ConnectPanorama;
import edu.uc.eh.service.QueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chojnasm on 7/15/15.
 */

@Controller
public class RestController {


    ConnectPanorama connectPanorama;
    ReplicateAnnotationRepository replicateAnnotationRepository;
    QueryService queryService;

    @Autowired
    public RestController(
            ConnectPanorama connectPanorama,
            ReplicateAnnotationRepository replicateAnnotationRepository,
            QueryService queryService) {
        this.connectPanorama = connectPanorama;
        this.replicateAnnotationRepository = replicateAnnotationRepository;
        this.queryService = queryService;
    }

    @RequestMapping(value = "/api-assays", method = RequestMethod.POST, consumes = "application/json")
    public
    @ResponseBody
    List<AssayRecord> tableAsJson(@RequestBody List<Query> tags) {
        return queryService.getAllAssays(tags);
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
    List<String> getTagsForAutocompletion(){
        List<ReplicateAnnotation> all = replicateAnnotationRepository.findAll();
        List<String> output = new ArrayList<>();
        for(ReplicateAnnotation ra : all){
            String label = ra.getPertIname();
            if(!output.contains(label))output.add(label);
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
            String label = ra.getPertIname();
            if(!output.contains(label))output.add(label);
        }
        return output.subList(0,5);
    }
}
