package com.fly.crawler.processor;

import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.entity.Person;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by F on 2017/7/1.
 * 豆瓣电影及人物信息爬虫工具类
 */
@Service
@Data
public class DouBanLogoProcessor implements PageProcessor {

    public static final String URL_FILM = "/subject/\\d+/";

    public static final String URL_PERSON = "/celebrity/\\d+/";


    public static List<Film> filmSaveQueue = new ArrayList<>();  //临时数据，每次批量保存后清空
    public static List<Film> savedFilms = new ArrayList<>(); //此次任务最终完成的数据，返回给前端

    public static List<Person> savedPersons= new ArrayList<>();
    public static List<Person> personSaveQueue= new ArrayList<>();  //临时数据，每次批量保存后清空

    public static int sleepTime = 30000;

    public static int ind = 1;
    public static String runningLog = "";

    public String runningRecord(){
        String ret = runningLog;
        runningLog = "";
        return ret;
    }


    @Autowired
    CrawlerService crawlerService;

    private Site site = Site
            .me()
            .setSleepTime(sleepTime)
            .setRetryTimes(1)
            .setTimeOut(6000)
            .setCharset("utf-8")
            .setDomain("movie.douban.com")
            .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
            .addHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
            .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.74");
//            .addCookie("gr_user_id","922cd5e7-60dc-4640-92ac-f15bf31d7a41")
//            .addCookie("as","https://movie.douban.com/")
//            .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3")
//            //.addHeader("Accept-Encoding", "gzip, deflate, br")
//            .addHeader("Accept-Language", "zh-CN,zh;q=0.9,en;q=0.8")
//            .addHeader("Connection", "keep-alive")
//            .addHeader("Cache-Control", "max-age=0");
            //.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:56.0) Gecko/20100101 Firefox/56.0");


    @Override
    public Site getSite() {
        return site;
    }

    @Override
    public void process(Page page) {

        //1)电影页面
        if (page.getUrl().regex(URL_FILM).match() ) {
            Film f = crawlerService.extractOnlyFilmLogo(page);
            filmSaveQueue.add(f);
        }else if(page.getUrl().regex(URL_PERSON).match()){
            Person p = crawlerService.extractOnlyPersonLogo(page);
            personSaveQueue.add(p);
        }else {
            System.out.println("--URL不符合Rule--"+page.getUrl());
            runningLog  = "--跳过----URL不符合Rule----"+page.getUrl();
        }

       //批量保存，而不是抓一个就保存一次
        crawlerService.saveFilmList(filmSaveQueue);
        crawlerService.savePersonList(personSaveQueue);

        //加入到savedPersons
        savedFilms.addAll(filmSaveQueue);
        savedPersons.addAll(personSaveQueue);

        filmSaveQueue.clear();
        personSaveQueue.clear();

    }







}
