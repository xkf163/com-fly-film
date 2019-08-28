package com.fly.crawler.service.impl;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.processor.DouBanProcessor;
import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.service.FilmService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;

import javax.annotation.PostConstruct;
import java.util.*;


@Service
public class CrawlerServiceImpl implements CrawlerService {

    @Autowired
    FilmService filmService;

    @Autowired
    DouBanProcessor douBanProcessor;

    public static Map<String,String> xPathMap = new HashMap<>();

    static {
        xPathMap.put("subject","//div[@id=\"content\"]/h1/span[1]/text()");
    }

    @Override
    public void running(Crawler crawler) {

        System.out.println(crawler.toString());

        douBanProcessor.filmSaveQueue=new ArrayList<>();
        douBanProcessor.savedFilms=new ArrayList<>();
        ///douBanProcessor.dbFilmsDouBanNo  = filmService.listFilmsDouBanNo();
        //douBanProcessor.dbPersonsDouBanNo = personService.listPersonsDouBanNo();
        douBanProcessor.directorAllowEmpty = crawler.getDirectorEmpty();
        douBanProcessor.actorAllowEmpty = crawler.getActorEmpty();
        //批量保存临界值
        douBanProcessor.setBatchNumber(Integer.parseInt(crawler.getBatchNumber()));

        String url  = crawler.getUrl();
        Integer thread = Integer.parseInt(crawler.getThread());
        //首页进入
        if ("1".equals(crawler.getHomepage())) {
            url = "https://movie.douban.com/";
            Spider.create(douBanProcessor).addUrl(url).thread(thread).run();
        }else{
            //转换成数组
            String[] targetUrls= url.split("\r\n");
            //默认spider
            Spider spider = Spider.create(douBanProcessor).addUrl(targetUrls).thread(thread);
            spider.run();
        }



    }


    @Override
    public Film extractFilm(Page page) {

        Html pageHtml = page.getHtml();
        Selectable filmInfoWrap = pageHtml.xpath("//*[@id=\"content\"]/div[2]/div[1]/div[1]/div[1]");


        Film f = new Film();
        //1）片名
        page.putField("subject", pageHtml.xpath(xPathMap.get("subject")).toString());
        f.setSubject(page.getResultItems().get("subject"));
        //2）导演
        f.setDirectors(StringUtils.join(filmInfoWrap.xpath("//a[@rel='v:directedBy']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));
        //3）演员
        f.setActors(StringUtils.join(filmInfoWrap.xpath("//a[@rel='v:starring']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));

        //4）豆瓣编号
        f.setDoubanNo(page.getUrl().regex("/subject/(\\d+)/").toString());

        //5、6）豆瓣评分及评分人数
        Selectable selectableRating = pageHtml.xpath("//div[@typeof='v:Rating']");
        PlainText object = (PlainText) selectableRating.xpath("//strong/text()");
        if (null != object && !"".equals(object.getFirstSourceText())) {
            f.setDoubanRating(Float.parseFloat(selectableRating.xpath("//strong/text()").toString()));
            f.setDoubanSum(Long.parseLong(selectableRating.xpath("//span[@property='v:votes']/text()").toString()));
        }
        //7)集数<span class="pl">集数:</span><br>
        String episodeNumber = filmInfoWrap.regex("<span class=\"pl\">集数:</span> (\\d+)\n"+
                " <br>").toString();
        if (null != episodeNumber && !"".equals(episodeNumber)) {
            f.setEpisodeNumber(episodeNumber);
        }

        //8）年代
        page.putField("year", page.getHtml().xpath("//div[@id='content']/h1//span[@class='year']/text()").regex("\\((.*)\\)"));
        if(page.getResultItems().get("year").toString()!=null)
            f.setYear(Short.parseShort(page.getResultItems().get("year").toString()));


        System.out.println("-------------crawler-------------");
        System.out.println(f);
        return f;
    }




    public Film extractFilm1(Page page) {
        Film f = new Film();
        //影片页
        //1）片名
        page.putField("subject", page.getHtml().xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)"));
        f.setSubject(page.getResultItems().get("subject").toString().trim());


        Selectable selectableInfo = page.getHtml().xpath("//div[@id='info']");
        //2）导演
        f.setDirectors(StringUtils.join(selectableInfo.xpath("//a[@rel='v:directedBy']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));
        //3）演员
        f.setActors(StringUtils.join(selectableInfo.xpath("//a[@rel='v:starring']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));

        //4）豆瓣编号
        f.setDoubanNo(page.getUrl().regex("/subject/(\\d+)/").toString());
        //5、6）豆瓣评分及评分人数
        Selectable selectableRating = page.getHtml().xpath("//div[@typeof='v:Rating']");
        PlainText object = (PlainText) selectableRating.xpath("//strong/text()");
        if (null != object && !"".equals(object.getFirstSourceText())) {
            f.setDoubanRating(Float.parseFloat(selectableRating.xpath("//strong/text()").toString()));
            f.setDoubanSum(Long.parseLong(selectableRating.xpath("//span[@property='v:votes']/text()").toString()));
        }
        //7)集数<span class="pl">集数:</span><br>
        String episodeNumber = selectableInfo.regex("<span class=\"pl\">集数:</span> (\\d+)\n"+
                " <br>").toString();
        if (null != episodeNumber && !"".equals(episodeNumber)) {
            f.setEpisodeNumber(episodeNumber);
        }
        //8）年代
        page.putField("year", page.getHtml().xpath("//div[@id='content']/h1//span[@class='year']/text()").regex("\\((.*)\\)"));
        if(page.getResultItems().get("year").toString()!=null)
            f.setYear(Short.parseShort(page.getResultItems().get("year").toString()));





        //校验数据是否符合抓取要求，非电影类（电视剧）不抓取
        //从网页中提取filmObject，只是部分字段，用于判断是否需要保存此object。

        return f;
    }


    @Override
    public void saveFilmList(List<Film> filmList) {
        filmService.batchInsertAndUpdate(filmList);
    }


}
