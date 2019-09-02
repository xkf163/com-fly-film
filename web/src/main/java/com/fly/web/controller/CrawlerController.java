package com.fly.web.controller;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.processor.DouBanProcessor;
import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.service.FilmService;
import com.fly.service.PersonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.monitor.SpiderMonitor;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping(value = "/crawler")
public class CrawlerController {

    @Autowired
    CrawlerService crawlerService;

    @Autowired
    FilmService filmService;

    @Autowired
    PersonService personService;

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
    public void running(Crawler crawler) throws JMException {

        //System.out.println(crawler.toString());

       // DouBanProcessor douBanProcessor = new DouBanProcessor();
       // douBanProcessor.directorAllowEmpty = crawler.getDirectorEmpty();
       // douBanProcessor.actorAllowEmpty = crawler.getActorEmpty();

        douBanProcessor.dbFilmDouBanNoList = filmService.findAllDouBanNo();
        douBanProcessor.dbPersonDouBanNoList = personService.findAllDouBanNo();
        //批量保存临界值
        douBanProcessor.setBatchNumber(Integer.parseInt(crawler.getBatchNumber()));
        //是延伸爬
        if("1".equals(crawler.getMutil())){
            douBanProcessor.setSingleCrawler(false);
        }

        //是否只爬取电影
        if("1".equals(crawler.getFilmOnly())){
           douBanProcessor.filmOnly = true;
        }
        //是否只爬取影人
        if("1".equals(crawler.getPersonOnly())){
            douBanProcessor.personOnly = true;
        }

        String url  = crawler.getUrl();
        Integer thread = Integer.parseInt(crawler.getThread());

        //首页进入
        if ("1".equals(crawler.getHomepage())) {
            url = "https://movie.douban.com/";
            spider = Spider.create(douBanProcessor).addUrl(url).thread(thread);

        }else{
            //转换成数组
            String[] targetUrls= url.split("\n");
            System.out.println(targetUrls.length);
            //默认spider
            spider = Spider.create(douBanProcessor).addUrl(targetUrls).thread(thread);

        }

        //SpiderMonitor.instance().register(spider);

        //异步启动，当前线程继续执行
        spider.start();

    }

    @GetMapping(value = "/stop")
    public String running(){

        if (spider != null){
            spider.stop();
        }

        return "views/crawler/new";

    }



}
