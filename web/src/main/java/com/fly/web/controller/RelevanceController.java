package com.fly.web.controller;

import com.fly.common.base.pojo.ResultBean;
import com.fly.crawler.entity.Crawler;
import com.fly.pojo.Relevance;
import com.fly.service.RelevanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


@Controller
@RequestMapping(value = "/relevance")
public class RelevanceController {


    @Autowired
    RelevanceService relevanceService;

    @GetMapping(value = "/new")
    public String relevantNew(){
        return "views/relevance/new";
    }


    @PostMapping(value = "/start")
    @ResponseBody
    public ResultBean<String> relevantStart(Relevance relevance) {
        return relevanceService.relevantFilmForMedia(relevance);
    }



}
