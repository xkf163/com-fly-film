package com.fly.web.controller;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.service.CrawlerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping(value = "/crawler")
public class CrawlerController {


    @Autowired
    CrawlerService crawlerService;


    @GetMapping(value = "/new")
    public String crawlerNew(){
        return "views/crawler/new";
    }


    /**
     * @Author: xukangfeng
     * @Description 通用爬取方法
     * @Date : 22:05 2017/10/27
     * @param : url 豆瓣单个电影页面地址
     * @param : thread 线程数
     */
    @PostMapping(value = "/running")
    @ResponseBody
    public void running(Crawler crawler){
        crawlerService.running(crawler);
    }

}
