package com.fly.crawler.service;


import com.fly.crawler.entity.Crawler;
import com.fly.entity.Film;
import com.fly.entity.Person;
import us.codecraft.webmagic.Page;

import java.util.List;

public interface CrawlerService {


    Film extractFilm(Page page, List<String> dbFilmDouBanNoList);

    Person extractPerson(Page page, List<String> dbPersonDouBanNoList);

    void saveFilmList(List<Film> filmList);

    void savePersonList(List<Person> personList);

    void addTargetRequests(Page page , String xPath, String regexRuleForUrl , String regexRuleForData, List<String> dbFilmDouBanNoList , String crawlerType );
}
