package com.fly.web.restcontroller;

import com.fly.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/series")
public class RestSeriesController {

    @Autowired
    SeriesService seriesService;

    @PostMapping(value = "/all")
    public Map<String, Object> seriesAll(String reqObj) throws Exception {
        return seriesService.findAll(reqObj);
    }

}