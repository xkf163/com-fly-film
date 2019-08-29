package com.fly.crawler.service.impl;

import com.fly.crawler.entity.Crawler;
import com.fly.crawler.processor.DouBanProcessor;
import com.fly.crawler.service.CrawlerService;
import com.fly.entity.Film;
import com.fly.entity.Person;
import com.fly.service.FilmService;
import com.fly.service.PersonService;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.helper.StringUtil;
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
    PersonService personService;

    public static Map<String,String> xPathMap = new HashMap<>();

    static {
        xPathMap.put("subject","//div[@id=\"content\"]/h1/span[1]/text()");
    }




    /*
    解析爬虫返回的页面数据，提取出film字段数据
     */
    @Override
    public Film extractFilm(Page page , List<String> dbFilmDouBanNoList) {

        //System.out.println("extracFilm");

        Html pageHtml = page.getHtml();
        Selectable filmInfoWrap = pageHtml.xpath("//div[@id='info']");

        //0)集数<span class="pl">集数:</span><br>
        String episodeNumber = filmInfoWrap.regex("<span class=\"pl\">集数:</span> (\\d+)\n"+
                " <br>").toString();
        if (null != episodeNumber && !"".equals(episodeNumber)) {
            //f.setEpisodeNumber(episodeNumber);
            return null; //如果是电视剧保存
        }



        Film f = new Film();
        String tempString;

        //4）豆瓣编号
        f.setDoubanNo(page.getUrl().regex("/subject/(\\d+)/").toString());
        if(dbFilmDouBanNoList.contains(f.getDoubanNo())){
            System.out.println("--->!!!film豆瓣编号"+f.getDoubanNo()+"在数据库已存在，不加入保存队列");
            return null; //数据库已存在该Film则返回空
        }


        //1）片名
        page.putField("subject", pageHtml.xpath(xPathMap.get("subject")).toString());
        f.setSubject(page.getResultItems().get("subject"));
        //2）导演
        f.setDirectors(StringUtils.join(filmInfoWrap.xpath("//a[@rel='v:directedBy']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));
        //3）演员
        f.setActors(StringUtils.join(filmInfoWrap.xpath("//a[@rel='v:starring']/@href").regex("/celebrity/(\\d+)/").all().toArray(), ","));




        //5、6）豆瓣评分及评分人数
        Selectable selectableRating = pageHtml.xpath("//div[@typeof='v:Rating']");
        PlainText object = (PlainText) selectableRating.xpath("//strong/text()");
        if (null != object && !"".equals(object.getFirstSourceText())) {
            f.setDoubanRating(Float.parseFloat(selectableRating.xpath("//strong/text()").toString()));
            f.setDoubanSum(Long.parseLong(selectableRating.xpath("//span[@property='v:votes']/text()").toString()));
        }

        //8）年代
        page.putField("year", page.getHtml().xpath("//div[@id='content']/h1//span[@class='year']/text()").regex("\\((.*)\\)"));
        if(page.getResultItems().get("year").toString()!=null)
            f.setYear(Short.parseShort(page.getResultItems().get("year").toString()));


        //Selectable selectableInfo = page.getHtml().xpath("//div[@id='info']");

        //9)
        page.putField("info", filmInfoWrap);
        f.setInfo(page.getResultItems().get("info").toString());

        //10)imdb编号
        String imdbNo = filmInfoWrap.regex("<a href=\"http://www.imdb.com/title/tt\\d+\" target=\"_blank\" rel=\"nofollow\">(tt\\d+)</a>").toString();
        f.setImdbNo(imdbNo);

        //11)其他片名
        page.putField("subjectMain", page.getHtml().xpath("//div[@id='content']/h1//span[@property='v:itemreviewed']/text()"));
        f.setSubjectMain(page.getResultItems().get("subjectMain").toString().trim());

        //12）影片简介
        page.putField("introduce", page.getHtml().xpath("//div[@class='related-info']//div[@class='indent']//span[@property='v:summary']/text()"));
        f.setIntroduce(page.getResultItems().get("introduce").toString());

        //13）影片类别
        f.setGenre(StringUtils.join(filmInfoWrap.xpath("//span[@property='v:genre']/text()").all().toArray(), ","));

        //14）发行日期
        f.setInitialReleaseDate(StringUtils.join(filmInfoWrap.xpath("//span[@property='v:initialReleaseDate']/text()").all().toArray(), ","));

        //15）影片时长
//        Object[] runtimeArray = (Object[]) filmInfoWrap.xpath("//span[@property='v:runtime']/@content").all().toArray();
//        f.setRuntimeFull(StringUtils.join(runtimeArray, ","));
//        if (runtimeArray.length == 1){
//            f.setRuntime(Short.parseShort((String) runtimeArray[0]));
//        }
        tempString = filmInfoWrap.xpath("//span[@property='v:runtime']/@content").toString();
        if (!StringUtils.isEmpty(tempString)){
            f.setRuntime(Short.parseShort(tempString));
        }



        //16）影片所属国家/地区
        String country_temp = filmInfoWrap.regex("<span class=\"pl\">制片国家/地区:</span> (.*)\n" +
                " <br>").toString();
        if (null != country_temp && !"".equals(country_temp)) {
            String country = country_temp.substring(0, country_temp.indexOf("\n"));
            f.setCountry(country);
        }

        //17）影片其他片名
        String subject_temp = filmInfoWrap.regex("<span class=\"pl\">又名:</span> (.*)\n" +
                " <br>").toString();
        if (null != subject_temp && !"".equals(subject_temp)) {
            if (subject_temp.indexOf("\n") > 0) {
                String subjectOther = subject_temp.substring(0, subject_temp.indexOf("\n"));
                f.setSubjectOther(subjectOther);
            } else {
                f.setSubjectOther(subject_temp);
            }
        }

        //System.out.println(f);
        return f;
    }


    @Override
    public Person extractPerson(Page page, List<String> dbPersonDouBanNoList) {
        Html pageHtml = page.getHtml();
        //Selectable filmInfoWrap = pageHtml.xpath("//div[@id='info']");

        page.putField("name", pageHtml.xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)").toString());
        page.putField("doubanNo", page.getUrl().regex("https://movie\\.douban\\.com/celebrity/(\\d+)/").toString());

        if("".equals(page.getResultItems().get("name"))){
            return null;
        }

        Person p  = new Person();
        //1)豆瓣编号
        p.setDoubanNo(page.getResultItems().get("doubanNo"));
        if("".equals(p.getDoubanNo())) {
            return null;
        }

        if(dbPersonDouBanNoList.contains(p.getDoubanNo())) {
            System.out.println("--->!!!Person豆瓣编号"+p.getDoubanNo()+"在数据库已存在，不加入保存队列");
            return null; //数据库已存在该Film则返回空
        }

        //2)姓名
        //page.putField("name", page.getHtml().xpath("//title/text()").regex("(.*)\\s*\\(豆瓣\\)"));
        p.setName(page.getResultItems().get("name"));
        //豆瓣编号
        //p.setDoubanNo(page.getUrl().regex("https://movie\\.douban\\.com/celebrity/(\\d+)/").toString());

        //人物脚本信息
        page.putField("nameExtend", pageHtml.xpath("//div[@id='content']/h1/text()"));
        page.putField("introduce", pageHtml.xpath("//div[@id='intro']//div[@class='bd']//span[@class='all hidden']/text()"));
        if(page.getResultItems().get("introduce").toString() == null){
            page.putField("introduce", page.getHtml().xpath("//*[@id=\"intro\"]/div[@class='bd']/text()"));
        }
        //人物简介字符串集合
        Selectable selectableInfo =  pageHtml.xpath("//div[@class='article']//div[@class='info']//li");
        page.putField("info", selectableInfo);

        String gender = StringUtils.join( selectableInfo.regex("<li> <span>性别</span>: (.*) </li>").all().toArray(),",");
        String birthday = StringUtils.join( selectableInfo.regex("<li> <span>出生日期</span>: (.*) </li>").all().toArray(),",");
        String birthplace = StringUtils.join( selectableInfo.regex("<li> <span>出生地</span>: (.*) </li>").all().toArray(),",");
        String profession = StringUtils.join( selectableInfo.regex("<li> <span>职业</span>: (.*) </li>").all().toArray(),",");
        String imdbNo = StringUtils.join( selectableInfo.regex("<li> <span>imdb编号</span>: (.*) </li>").regex("<a href=\"http://www.imdb.com/name/nm\\d+\" target=\"_blank\">(nm\\d+)</a>").all().toArray(),",");

        p.setNameExtend(page.getResultItems().get("nameExtend").toString());
        p.setIntroduce(page.getResultItems().get("introduce").toString());
        p.setInfo(page.getResultItems().get("info").toString());

        p.setBirthDay(birthday);
        p.setJob(profession);
        p.setGender(gender);
        p.setImdbNo(imdbNo);
        p.setBirthPlace(birthplace);

        return p;
    }

    @Override
    public void addTargetRequests(Page page , String xPath, String regexRuleForUrl , String regexRuleForData , List<String> dbDouBanNoList ,String crawlerType){
            //2）后续的电影url，有10个
            //2.1)取出后续电影doubannNo LIST，判断dbFilmsDouBanNoList是否已存在，已存在就不add了
        System.out.println("---------------addTargetRequests--------"+crawlerType+"------------");
        Selectable selectable ;
        if ("xpath".equals(crawlerType)) {
             selectable = page.getHtml().xpath(xPath).links().regex(regexRuleForUrl);
        }else{
             selectable = page.getHtml().css(xPath).links().regex(regexRuleForUrl);
        }
        List<String> crawlerQueue = filterUrl(selectable,regexRuleForData,dbDouBanNoList);
        page.addTargetRequests(crawlerQueue);
    }


    public List<String> filterUrl(Selectable selectable,String regexRule,List<String> dbFilmDouBanNoList){
        //原始urls
        List<String> oriUrlList =selectable.all(); //影片页面中的 符合条件的相关影片URLS
        System.out.println("---------------filterUrl--------------------"+oriUrlList.size());

        List<String> oriDouBanNoList =selectable.regex(regexRule).all(); //提取oriUrlList 中的 豆瓣no，供后续唯一性判断用
        List<String> filmQueue = new ArrayList<>(oriUrlList);
        int i;
        for (i=0;i <oriDouBanNoList.size(); i++){
            if(dbFilmDouBanNoList.contains(oriDouBanNoList.get(i))){
                System.out.println("--->!!!film豆瓣编号"+oriDouBanNoList.get(i)+"在数据库已存在，不加入抓取队列");
                filmQueue.remove(oriUrlList.get(i));
            }
        }
        return filmQueue;
    }





    /*
    批量保存Film
     */
    @Override
    public void saveFilmList(List<Film> filmList) {
        filmService.batchInsertAndUpdate(filmList);
    }

    /*
    批量保存Person
     */
    @Override
    public void savePersonList(List<Person> personList) {
        personService.batchInsertAndUpdate(personList);
    }


}
