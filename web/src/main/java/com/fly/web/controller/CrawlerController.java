package com.fly.web.controller;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.processor.DouBanProcessor;
import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.service.FilmService;
import com.fly.service.PersonService;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.SpiderListener;
import us.codecraft.webmagic.monitor.SpiderMonitor;

import javax.management.JMException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

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

        douBanProcessor.dbFilmDouBanNoList = filmService.findAllDouBanNo();
        douBanProcessor.dbPersonDouBanNoList = personService.findAllDouBanNo();
        //批量保存临界值
        douBanProcessor.setBatchNumber(Integer.parseInt(crawler.getBatchNumber()));

        douBanProcessor.getSite().setSleepTime( crawler.getSleepTime()) ;


        //是延伸爬
        if("1".equals(crawler.getLoginIn())){

            douBanProcessor.getSite().setUserAgent("Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/76.0.3809.100 Safari/537.36")
                    .addCookie("__utma", "30149280.1980906895.1559658144.1568516128.1568516244.14")
                    .addCookie("__utma", "223695111.1182102876.1559658144.1568516128.1568516245.10")
                    .addCookie("__utmb", "30149280.0.10.1568516244")
                    .addCookie("__utmb", "223695111.0.10.1568516245")
                    .addCookie("__utmc", "30149280")
                    .addCookie("__utmc", "223695111")
                    .addCookie("__utmt", "1")
                    .addCookie("__utmz", "30149280.1568516244.14.10.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/passport/login")
                    .addCookie("__utmz", "223695111.1568516245.10.8.utmcsr=accounts.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/passport/login")
                    .addCookie("__yadk_uid", "MLav7bmXG4MFd6vpKEfx1yzy0j9OxWSt")
                    .addCookie("_pk_id.100001.4cf6", "7813fef5453dc078.1555408943.12.1568516244.1568516223.")
                    .addCookie("_pk_ref.100001.4cf6", "%5B%22%22%2C%22%22%2C1568516244%2C%22https%3A%2F%2Faccounts.douban.com%2Fpassport%2Flogin%22%5D")
                    .addCookie("_pk_ses.100001.4cf6", "*")
                    .addCookie("_vwo_uuid_v2", "DE8E99BF6B7BB8C96A6937774B27E2217|29f884e8256e4324bb08c187a593170a")
                    .addCookie("acw_tc", "2760828a15680895477653210ee153816ab74aff939b612c7a7c5cb5325a4b")
                    .addCookie("ap_v", "0,6.0")
                    .addCookie("bid", "X3nxgVw2IyE")
                    .addCookie("ck", "kFbH")
                    .addCookie("ct", "y")
                    .addCookie("dbcl2", "173941351:oqncRyWS8Zc")
                    .addCookie("douban-fav-remind", "1")
                    .addCookie("ll", "118172")
                    .addCookie("push_doumail_num", "0")
                    .addCookie("push_noty_num", "0")
                    .addCookie("regpop", "1")
                    .addCookie("trc_cookie_storage", "taboola%2520global%253Auser-id%3D27e39c7d-d4b7-4d84-a21a-7b7143311c4c-tuct34086e6")
                    .addCookie("Referer", "movie.douban.com")
                    .addCookie("viewed","\"3439300\"");

        }else {
            douBanProcessor.setSite(Site.me().setSleepTime( crawler.getSleepTime())
                    .setRetryTimes(1)
                    .setTimeOut(6000)
                    .setCharset("utf-8")
                    .setDomain("movie.douban.com"));
        }


        //是延伸爬
        douBanProcessor.setSingleCrawler(true);
        if("1".equals(crawler.getMutil())){
            douBanProcessor.setSingleCrawler(false);
        }

        //是否只爬取电影
        douBanProcessor.filmOnly = false;
        if("1".equals(crawler.getFilmOnly())){
           douBanProcessor.filmOnly = true;
        }
        //是否只爬取影人
        douBanProcessor.personOnly = false;
        if("1".equals(crawler.getPersonOnly())){
            douBanProcessor.personOnly = true;
        }

        //是否只爬取影人
        douBanProcessor.ratingAllowEmpty = false;
        if("1".equals(crawler.getRatingEmpty())){
            douBanProcessor.ratingAllowEmpty = true;
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
        douBanProcessor.ind = 1;
        douBanProcessor.runningLog = "";

        return "views/crawler/new";

    }




    @PostMapping(value = "/log")
    @ResponseBody
    public String crawlerLog() {

        return douBanProcessor.runningRecord();
    }

}
