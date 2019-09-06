package com.fly.web.restcontroller;

import com.fly.entity.Star;
import com.fly.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping(value = "/api/star")
public class RestStarController {

    @Autowired
    StarService starService;


    @PostMapping(value = "/all")
    public Map<String, Object> personAll(String reqObj) throws Exception {
        return starService.findAll(reqObj);
    }


    @PostMapping(value = "/get")
    private Star getStar(String id) {
        return starService.findOne(Long.parseLong(id));
    }



}
