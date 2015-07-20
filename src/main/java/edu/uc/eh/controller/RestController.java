package edu.uc.eh.controller;

import edu.uc.eh.service.ConnectPanorama;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by chojnasm on 7/15/15.
 */

@Controller
public class RestController {

    @Autowired
    ConnectPanorama connectPanorama;

    @RequestMapping(value = "/api-panorama", method = RequestMethod.GET)
    public
    @ResponseBody
    List<String> gctUrlsFromPanorama() throws Exception {
        return connectPanorama.GctUrls();
    }
}
