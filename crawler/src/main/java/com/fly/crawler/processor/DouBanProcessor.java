package com.fly.crawler.processor;

import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.entity.Person;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Selectable;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;


/**
 * Created by F on 2017/7/1.
 * 豆瓣电影及人物信息爬虫工具类
 */
@Service
@Data
public class DouBanProcessor implements PageProcessor {

    public static final String URL_FILM = "/subject/\\d+/";
    //  https://movie.douban.com/subject/1291839/?from=subject-page
    public static final String URL_FILM_FROM_SUBJECT_PAGE = "/subject/\\d+/\\?from=subject-page";
    //豆瓣首页
    public static final String URL_FILM_FROM_SHOWING = "/subject/\\d+/\\?from=showing";

    public static final String URL_FILM_FROM_REVIEWS = "/subject/\\d+/\\?from=reviews";
    //https://movie.douban.com/subject/24753477/?tag=%E7%83%AD%E9%97%A8&from=gaia
    public static final String URL_FILM_FROM_HOT = "https://movie\\.douban\\.com/subject/\\d+/\\?tag=.*&from=.*";

    public static final String URL_PERSON = "/celebrity/\\d+/";
    public static final String URL_PERSON_FULL = "https://movie\\.douban\\.com/celebrity/\\d+/";

    public static final String URL_HOMEPAGE = "https://movie\\.douban\\.com";

    public static final String URL_FILM_SEARCH = "https://movie\\.douban\\.com/subject_search";

    public static List<Film> filmSaveQueue = new ArrayList<>();  //临时数据，每次批量保存后清空
    public static List<Film> savedFilms = new ArrayList<>(); //此次任务最终完成的数据，返回给前端

    public static List<Person> savedPersons= new ArrayList<>();
    public static List<Person> personSaveQueue= new ArrayList<>();  //临时数据，每次批量保存后清空

    public static List<String> dbPersonDouBanNoList; //数据库已存persons的doubanno
    public static List<String> dbFilmDouBanNoList; //数据库已存persons的doubanno

    public static List<String> filmDouBanNoQueue = new ArrayList<>();  //保存队列中Film的豆瓣NO，防止Film重复加入filmSaveQueue
    public static List<String> personDouBanNoQueue = new ArrayList<>();

    public static boolean actorAllowEmpty = false;
    public static boolean directorAllowEmpty = false;

    public static boolean ratingAllowEmpty = false; //豆分

    public static boolean filmOnly = false; //只爬取电影
    public static boolean personOnly = false;

    public static boolean patchBirthday = false;
    public static boolean patchNameOther = false;

    //爬虫是否单个电影爬取，默认单个爬取完成后就结束；false即无限延伸爬取，时间比较长
    public boolean singleCrawler = true;

    //批量保存临界个数
    public int batchNumber = 10;

    public static int sleepTime = 10000;


    @Autowired
    CrawlerService crawlerService;

    private Site site = Site
            .me()
            .setSleepTime(sleepTime)
            .setRetryTimes(1)
            .setTimeOut(6000)
            .setCharset("utf-8")
            .setDomain("movie.douban.com");

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

            //3种情况
            //1)personOnly = false; filmOnly = false
            //2)personOnly = true; filmOnly = false
            //3)personOnly = false; filmOnly = true

            Film f = null;
            if (!personOnly){
                f = crawlerService.extractFilm(page, dbFilmDouBanNoList,ratingAllowEmpty);
            }

            if (f != null){
                //doubanno不存在 filmDouBanNoQueue 中才能加入保存队列，防止重复加入
                String douBanNo = f.getDoubanNo();
                if (!filmDouBanNoQueue.contains(douBanNo)){
                    filmSaveQueue.add(f);
                    filmDouBanNoQueue.add(douBanNo);
                }
                if(!filmOnly){
                    //1)把页面中的相关人物URL加入到爬取队列中
                    crawlerService.addTargetRequests(page, "div.subject.clearfix" , URL_PERSON,"/celebrity/(\\d+)/" , dbPersonDouBanNoList , "csspath", "Person");
                }

                //是否延伸爬
                if(!this.singleCrawler && !personOnly){
                    //2)把页面中的相关影片URL加入到爬取队列中
                    crawlerService.addTargetRequests(page, "//div[@class='recommendations-bd']/dl/dt" ,URL_FILM_FROM_SUBJECT_PAGE, "/subject/(\\d+)/" , dbFilmDouBanNoList , "xpath", "Film");
                }
            }else if(f == null && !personOnly){
                //2)把页面中的相关影片URL加入到爬取队列中
                crawlerService.addTargetRequests(page, "//div[@class='recommendations-bd']/dl/dt" ,URL_FILM_FROM_SUBJECT_PAGE, "/subject/(\\d+)/" , dbFilmDouBanNoList , "xpath", "Film");

            }else if(f == null && !filmOnly){
                //1)把页面中的相关人物URL加入到爬取队列中
                crawlerService.addTargetRequests(page, "div.subject.clearfix" , URL_PERSON,"/celebrity/(\\d+)/" , dbPersonDouBanNoList , "csspath", "Person");
            }else{
                System.out.println("--->!!! Film Skip");
                page.setSkip(true);
            }

        }else if(page.getUrl().regex(URL_PERSON).match()){


            //3种情况
            //1)personOnly = false; filmOnly = false
            //2)personOnly = true; filmOnly = false
            //3)personOnly = false; filmOnly = true
            Person p = null;
            if (!filmOnly){
                p = crawlerService.extractPerson(page, dbPersonDouBanNoList);
            }


            if (p != null){
                //doubanno不存在 filmDouBanNoQueue 中才能加入保存队列，防止重复加入
                String douBanNo = p.getDouBanNo();
                if (!personDouBanNoQueue.contains(douBanNo)){
                    personSaveQueue.add(p);
                    personDouBanNoQueue.add(douBanNo);
                }

                if(!filmOnly){
                    //合作2次以上的影人  · · · · ·
                    crawlerService.addTargetRequests(page, "//*[@id=\"partners\"]/div[@class=\"bd\"]/ul[@class=\"list-s\"]/li/div[@class=\"pic\"]" ,URL_PERSON, "/celebrity/(\\d+)/" , dbPersonDouBanNoList , "xpath", "Person");
                }

                //是否延伸爬
                if(!this.singleCrawler && !filmOnly){
                    crawlerService.addTargetRequests(page, "//*[@id=\"recent_movies\"]/div[@class=\"bd\"]/ul[@class=\"list-s\"]/li/div[@class=\"pic\"]" ,URL_FILM, "/subject/(\\d+)/" , dbFilmDouBanNoList , "xpath", "Film");
                }

            }else if(p == null && !personOnly){
                //把人物页面的 影片加入队列
                crawlerService.addTargetRequests(page, "//*[@id=\"recent_movies\"]/div[@class=\"bd\"]/ul[@class=\"list-s\"]/li/div[@class=\"pic\"]" ,URL_FILM, "/subject/(\\d+)/" , dbFilmDouBanNoList , "xpath", "Film");
            }else if(p == null && !filmOnly){
                //合作2次以上的影人
                crawlerService.addTargetRequests(page, "//*[@id=\"partners\"]/div[@class=\"bd\"]/ul[@class=\"list-s\"]/li/div[@class=\"pic\"]" ,URL_PERSON, "/celebrity/(\\d+)/" , dbPersonDouBanNoList , "xpath", "Person");

            }else {
                page.setSkip(true);
            }



        }else if(page.getUrl().regex(URL_FILM_SEARCH).match()){
            //System.out.println(page.getHtml());
            //无效：动态页面 抓取不到
            //查询结果页面第一页的所有链接加入队列
            //crawlerService.addTargetRequests(page, "//*[@id=\"wrapper\"]", URL_FILM, "/subject/(\\d+)/"  , dbFilmDouBanNoList , "xpath");

        }else if(page.getUrl().regex(URL_HOMEPAGE).match()){
            System.out.println("---------入口：豆瓣首页----------");
            crawlerService.addTargetRequests(page, "//*[@id=\"content\"]", URL_FILM, "/subject/(\\d+)/"  , dbFilmDouBanNoList , "xpath", "Film");

            //3)入口是豆瓣主页
            //3.1)正在热映
            //page.addTargetRequests(page.getHtml().xpath("//*[@id=\"screening\"]/div[2]/ul/li/ul/li[2]").links().regex(URL_FILM_FROM_SHOWING).all());
            //crawlerService.addTargetRequests(page, "//*[@id=\"screening\"]/div[2]/ul/li/ul/li[2]", URL_FILM_FROM_SHOWING, "/subject/(\\d+)/"  , dbFilmDouBanNoList , "xpath");
            //2)最近热门电影:貌似是动态生成，抓不到
            //page.addTargetRequests(page.getHtml().xpath("//*[@id="content"]/div/div[2]/div[4]/div[3]/div/div[1]").links().regex(URL_FILM_FROM_HOT).all());
            //3)一周口碑榜
            //crawlerService.addTargetRequests(page, "//*[@class=\"billboard-bd\"]", URL_FILM, "/subject/(\\d+)/"  , dbFilmDouBanNoList , "xpath");
            //4)最受欢迎的影评
            //crawlerService.addTargetRequests(page, "//*[@class=\"reviews-bd\"]", URL_FILM, "/subject/(\\d+)/"  , dbFilmDouBanNoList , "xpath");

        }else {
            System.out.println("--URL不符合Rule--"+page.getUrl());
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
