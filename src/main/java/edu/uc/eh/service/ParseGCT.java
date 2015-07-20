package edu.uc.eh.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.ArrayList;

/**
 * Created by chojnasm on 7/14/15.
 */

@Service
public class ParseGCT {
    Logger logger = LoggerFactory.getLogger("edu.uc.eh.service.ParseGCT");

    public void DbToGct(){
//        TODO
    }

    public void GctToMemory(URL url,
                            ArrayList<String> values,
                            ArrayList<String> replicateAnnotations,
                            ArrayList<String> peptideAnnotations){
        logger.info("Processing: {}","");

    }
}
