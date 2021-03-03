package com.fly.web.controller;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.processor.DouBanLogoProcessor;
import com.fly.crawler.processor.DouBanProcessor;
import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.entity.Person;
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
import us.codecraft.webmagic.pipeline.FilePipeline;

import javax.management.JMException;
import javax.servlet.http.HttpServletRequest;
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

    @Autowired
    DouBanLogoProcessor douBanLogoProcessor;

    public static Spider spider;


    @GetMapping(value = "/new")
    public String crawlerNew(){
        return "views/crawler/new";
    }

    @GetMapping(value = "/picpatch")
    public String crawlerPicPatch(){
        return "views/crawler/picpatch";
    }


    /**
     * 人物作品海报爬取补丁
     * @param request
     * @throws JMException
     */
    @PostMapping(value = "/person/picpatch")
    @ResponseBody
    public void personPicPatch(HttpServletRequest request) {
        //nameExtend 关键字
        String searchKey = request.getParameter("searchKey");
        System.out.println("searchKey:" +searchKey);
        List<String> fs = filmService.findAllOfPerson(searchKey);

        String[] urlArray=fs.toArray(new String[fs.size()]);

        douBanLogoProcessor.runningLog  = " 海报爬取队列长度： "+fs.size();

        System.out.println(" 海报爬取队列长度： "+fs.size());

        //默认spider
        spider = Spider.create(douBanLogoProcessor).addUrl(urlArray).thread(1);

        //异步启动，当前线程继续执行
        spider.start();

    }


    /**
     * 电影人物头像爬取补丁
     * @param request
     */
    @PostMapping(value = "/film/picpatch")
    @ResponseBody
    public void filmPicPatch(HttpServletRequest request)  {
        String searchKey = request.getParameter("searchKey");

        System.out.println("searchKey:" +searchKey);
        List<String> fs = personService.findAllOfFilm(searchKey);

        String[] urlArray=fs.toArray(new String[fs.size()]);
        douBanLogoProcessor.runningLog  = " 头像爬取队列长度： "+fs.size();
        System.out.println(" 头像爬取队列长度： "+fs.size());
        //默认spider
        spider = Spider.create(douBanLogoProcessor).addUrl(urlArray).thread(1);

        //异步启动，当前线程继续执行
        spider.start();



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
            douBanProcessor.getSite().setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.74")
//                    .addHeader("Upgrade-Insecure-Requests","1")
//                    .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
//                    .addHeader("Host","movie.douban.com")
//                    .addHeader("Sec-Fetch-Site","none")
//                    .addHeader("Sec-Fetch-Mode","navigate")
//                    .addHeader("Sec-Fetch-User","?1")
//                    .addHeader("Sec-Fetch-Dest","document")
//                    .addHeader("Accept-Encoding","gzip, deflate, br")
//                    .addHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
//                    .addHeader("Cookie","__yadk_uid=NjVEYHUjY3FDdnZ4wSUyV2Ba5FATwU6s; ll=\"118172\"; bid=QgZxwfZoHes; _vwo_uuid_v2=D30B3CE5F0F73F2AE8B10A94BC94893D6|9964dc4949af23699f8400e199f988cf; __utmc=30149280; __utmc=223695111; __gads=ID=81f64bb146d609fd-220548754dc300df:T=1599097847:RT=1599097847:R:S=ALNI_Mbozr8JyynTkJsgR2HHaKSOFwQCug; __utmz=30149280.1614135138.4.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmz=223695111.1614135138.4.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; _pk_ref.100001.4cf6=[\"\",\"\",1614142023,\"https://www.baidu.com/link?url=wuvXb1l_QZ7oHcqgEJtd28pmkHsoNHLFLe6oaO83kYTArNCrWN7d-FJrYdfuvnc9&wd=&eqid=b7b22a2e0002f0a5000000046035bc22\"]; _pk_ses.100001.4cf6=*; __utma=30149280.1537919671.1599096252.1614139946.1614142024.6; __utmb=30149280.0.10.1614142024; __utma=223695111.1671921046.1599096252.1614139946.1614142024.6; __utmb=223695111.0.10.1614142024; ap_v=0,6.0; _pk_id.100001.4cf6=b2081f63c2d74a8c.1599096254.5.1614142326.1614139946.");


            //__yadk_uid=NjVEYHUjY3FDdnZ4wSUyV2Ba5FATwU6s; ll="118172"; bid=QgZxwfZoHes; _vwo_uuid_v2=D30B3CE5F0F73F2AE8B10A94BC94893D6|9964dc4949af23699f8400e199f988cf;
                    // __utmc=30149280; __utmc=223695111; __gads=ID=81f64bb146d609fd-220548754dc300df:T=1599097847:RT=1599097847:R:S=ALNI_Mbozr8JyynTkJsgR2HHaKSOFwQCug;
                    // __utmz=30149280.1614135138.4.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic; __utmz=223695111.1614135138.4.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic;
                    // _pk_ref.100001.4cf6=["","",1614142023,"https://www.baidu.com/link?url=wuvXb1l_QZ7oHcqgEJtd28pmkHsoNHLFLe6oaO83kYTArNCrWN7d-FJrYdfuvnc9&wd=&eqid=b7b22a2e0002f0a5000000046035bc22"];
                    // _pk_ses.100001.4cf6=*; __utma=30149280.1537919671.1599096252.1614139946.1614142024.6; __utmb=30149280.0.10.1614142024;
                    // __utma=223695111.1671921046.1599096252.1614139946.1614142024.6; __utmb=223695111.0.10.1614142024; ap_v=0,6.0; _pk_id.100001.4cf6=b2081f63c2d74a8c.1599096254.5.1614142326.1614139946.




                    .addCookie("__yadk_uid", "NjVEYHUjY3FDdnZ4wSUyV2Ba5FATwU6s")
                    .addCookie("ll", "118172")
                    .addCookie("bid", "QgZxwfZoHes")
                    .addCookie("_vwo_uuid_v2", "D30B3CE5F0F73F2AE8B10A94BC94893D6|9964dc4949af23699f8400e199f988cf")
                    .addCookie("__utmc", "30149280")
                    .addCookie("__utmc", "223695111")
                    .addCookie("__gads", "ID=81f64bb146d609fd-220548754dc300df:T=1599097847:RT=1599097847:R:S=ALNI_Mbozr8JyynTkJsgR2HHaKSOFwQCug")
                    .addCookie("__utmz", "30149280.1614135138.4.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic")
                    .addCookie("__utmz", "223695111.1614135138.4.2.utmcsr=baidu|utmccn=(organic)|utmcmd=organic")
                    .addCookie("_pk_id.100001.4cf6", "[\"\",\"\",1614142023,\"https://www.baidu.com/link?url=wuvXb1l_QZ7oHcqgEJtd28pmkHsoNHLFLe6oaO83kYTArNCrWN7d-FJrYdfuvnc9&wd=&eqid=b7b22a2e0002f0a5000000046035bc22\"]")
                    .addCookie("_pk_ses.100001.4cf6", "*")
                    .addCookie("__utma", "30149280.1537919671.1599096252.1614139946.1614142024.6")
                    .addCookie("__utmb", "30149280.0.10.1614142024")
                    .addCookie("__utma", "223695111.1671921046.1599096252.1614139946.1614142024.6")
                    .addCookie("__utmb", "223695111.0.10.1614142024")
                    .addCookie("ap_v", "0,6.0")
                    .addCookie("_pk_id.100001.4cf6", "b2081f63c2d74a8c.1599096254.5.1614142326.1614139946.");

//                    .addCookie("__utmt", "1")
//                    .addCookie("_pk_ref.100001.4cf6", "%5B%22%22%2C%22%22%2C1568516244%2C%22https%3A%2F%2Faccounts.douban.com%2Fpassport%2Flogin%22%5D")
//                    .addCookie("acw_tc", "2760828a15680895477653210ee153816ab74aff939b612c7a7c5cb5325a4b")
//                    .addCookie("ck", "kFbH")
//                    .addCookie("ct", "y")
//                    .addCookie("dbcl2", "173941351:oqncRyWS8Zc")
//                    .addCookie("douban-fav-remind", "1")
//                    .addCookie("push_doumail_num", "0")
//                    .addCookie("push_noty_num", "0")
//                    .addCookie("regpop", "1")
//                    .addCookie("trc_cookie_storage", "taboola%2520global%253Auser-id%3D27e39c7d-d4b7-4d84-a21a-7b7143311c4c-tuct34086e6")
//                    .addCookie("Referer", "movie.douban.com")
//                    .addCookie("viewed","\"3439300\"");

        }else {
            douBanProcessor.setSite(Site.me().setSleepTime(crawler.getSleepTime())
                    .setRetryTimes(1)
                    .setTimeOut(10000)
                    .setCharset("utf-8")
                    .setDomain("movie.douban.com")
                    .addHeader("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
                    .addHeader("Accept-Language","zh-CN,zh;q=0.9,en;q=0.8,en-GB;q=0.7,en-US;q=0.6")
//                    .addHeader("Cache-Control","no-cache")
//                    .addHeader("Connection","keep-alive")
//                    .addHeader("Cookie","ll=\"118172\"; bid=PIOdFoxdFzo; __yadk_uid=9LCNZjYykWwW7VaMlchyMCyd5owv5m89; __gads=ID=a793a9979f6c3c34-2259baf57ec40023:T=1604153151:RT=1604153151:S=ALNI_MbKKQbjlSbqMLyLL3Nom5VhhX3AVg; _vwo_uuid_v2=DD0C20C1E8E4C1FDC683726B4973C31F2|2cc30f7c755a860a848624a82540a079; douban-fav-remind=1; __utmz=30149280.1613906322.5.5.utmcsr=cn.bing.com|utmccn=(referral)|utmcmd=referral|utmcct=/; __utmz=223695111.1613906322.3.3.utmcsr=cn.bing.com|utmccn=(referral)|utmcmd=referral|utmcct=/; ap_v=0,6.0; __utmc=30149280; __utmc=223695111; _pk_ref.100001.4cf6=%5B%22%22%2C%22%22%2C1614261720%2C%22https%3A%2F%2Fcn.bing.com%2F%22%5D; _pk_id.100001.4cf6=2b5f13d2e45374f6.1604153151.8.1614261720.1614257645.; _pk_ses.100001.4cf6=*; __utma=30149280.465811318.1604153151.1614257639.1614261720.10; __utmb=30149280.0.10.1614261720; __utma=223695111.1716771773.1604153151.1614257639.1614261720.8; __utmb=223695111.0.10.1614261720")
//                    .addHeader("Host","movie.douban.com")
//                    .addHeader("Pragma","no-cache")
//                    .addHeader("Referer","https://movie.douban.com/%20,")
//                    .addHeader("Sec-Fetch-Dest","document")
//                    .addHeader("Sec-Fetch-Mode","navigate")
//                    .addHeader("Sec-Fetch-Site","same-origin")
//                    .addHeader("Sec-Fetch-User","?1")
//                    .addHeader("Upgrade-Insecure-Requests","1")
                    .setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/88.0.4324.182 Safari/537.36 Edg/88.0.705.74")
            );
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

        douBanLogoProcessor.ind = 1;
        douBanLogoProcessor.runningLog = "";
        return "views/crawler/new";
    }

    @PostMapping(value = "/log")
    @ResponseBody
    public String crawlerLog() {
        return douBanProcessor.runningRecord();
    }


    @PostMapping(value = "/logb")
    @ResponseBody
    public String crawlerLogB() {
        return douBanLogoProcessor.runningRecord();
    }

    /**
     * @Author: xukangfeng
     * @Description 通用爬取方法
     * @Date : 22:05 2017/10/27
     * @param : url 豆瓣单个电影页面地址
     * @param : thread 线程数
     */
    @PostMapping(value = "/logo")
    @ResponseBody
    public void runningLogo(Crawler crawler) {
        //所有filmlogo为空的film数据，并返回 https://movie.douban.com/subject/1305579/ 的LIST，供爬虫用
        List<String> filmUrlWithoutLogoList = new ArrayList<>();
        if("1".equals(crawler.getForFilm())){
            filmUrlWithoutLogoList = filmService.findAllUrlOfFilmWithoutLogo();
        }else {
            filmUrlWithoutLogoList = filmService.findAllUrlOfMediaFilmWithoutLogo();
        }

        System.out.println("filmUrlWithoutLogoList :"+filmUrlWithoutLogoList.size());

        String[] urlArray=filmUrlWithoutLogoList.toArray(new String[filmUrlWithoutLogoList.size()]);

        douBanLogoProcessor.runningLog  = " 海报爬取队列长度： "+filmUrlWithoutLogoList.size();

        //默认spider
        spider = Spider.create(douBanLogoProcessor).addUrl(urlArray).thread(1);

        //SpiderMonitor.instance().register(spider);
        //异步启动，当前线程继续执行
        spider.start();

    }



    @PostMapping(value = "/logoP")
    @ResponseBody
    public void runningLogoP(Crawler crawler) {

        // System.out.println(crawler.getForFilm());

        String filmNumber = crawler.getFilmNumber();

        //所有filmlogo为空的film数据，并返回 https://movie.douban.com/subject/1305579/ 的LIST，供爬虫用
        List<String> personImportWithoutLogoList ;

        personImportWithoutLogoList = personService.findImportWithoutLogoList(filmNumber);


        System.out.println(" 人物海报爬取队列长度： "+personImportWithoutLogoList.size());

        String[] urlArray=personImportWithoutLogoList.toArray(new String[personImportWithoutLogoList.size()]);

        douBanLogoProcessor.runningLog  = " 人物海报爬取队列长度： "+personImportWithoutLogoList.size();

        //默认spider
        spider = Spider.create(douBanLogoProcessor).addUrl(urlArray).thread(1);

        //SpiderMonitor.instance().register(spider);
        //异步启动，当前线程继续执行
        spider.start();

    }

}
