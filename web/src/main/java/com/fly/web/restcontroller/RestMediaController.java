package com.fly.web.restcontroller;


import com.fly.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/media")
public class RestMediaController {

    @Autowired
    MediaService mediaService;

    @PostMapping(value = "/all")
    public Map<String, Object> mediaAll(String reqObj) throws Exception {
        return mediaService.findAll(reqObj);
    }
}
