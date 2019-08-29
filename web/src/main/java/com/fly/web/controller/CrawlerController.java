package com.fly.web.controller;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.processor.DouBanProcessor;
import com.fly.crawler.service.CrawlerService;
import com.fly.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.codecraft.webmagic.Spider;

import java.util.ArrayList;

@Controller
@RequestMapping(value = "/crawler")
public class CrawlerController {

    @Autowired
    CrawlerService crawlerService;

    @Autowired
    FilmService filmService;

    @Autowired
    DouBanProcessor douBanProcessor;

    public static Spider spider;


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
    @PostMapping(value = "/start")
    @ResponseBody
    public void running(Crawler crawler){

        System.out.println(crawler.toString());

       // DouBanProcessor douBanProcessor = new DouBanProcessor();
        douBanProcessor.directorAllowEmpty = crawler.getDirectorEmpty();
        douBanProcessor.actorAllowEmpty = crawler.getActorEmpty();
        douBanProcessor.dbFilmDouBanNoList = filmService.findAllDouBanNo();
        //批量保存临界值
        douBanProcessor.setBatchNumber(Integer.parseInt(crawler.getBatchNumber()));

        String url  = crawler.getUrl();
        Integer thread = Integer.parseInt(crawler.getThread());

        //首页进入
        if ("1".equals(crawler.getHomepage())) {
            url = "https://movie.douban.com/";
            spider = Spider.create(douBanProcessor).addUrl(url).thread(thread);

        }else{
            //转换成数组
            String[] targetUrls= url.split("\r\n");
            //默认spider
            spider = Spider.create(douBanProcessor).addUrl(targetUrls).thread(thread);

        }
        //异步启动，当前线程继续执行
        spider.start();

    }

    @PostMapping(value = "/stop")
    public void running(){

        if (spider != null){
            spider.stop();
        }

    }



}
