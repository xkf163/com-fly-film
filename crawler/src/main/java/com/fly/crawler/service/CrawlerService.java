package com.fly.crawler.service;


import com.fly.crawler.entity.Crawler;
import com.fly.entity.Film;
import us.codecraft.webmagic.Page;

import java.util.List;

public interface CrawlerService {


    Film extractFilm(Page page, List<String> dbFilmDouBanNoList);

    void saveFilmList(List<Film> filmList);

    void addTargetRequests(Page page , String xPath,String URL_FILM_FROM_SUBJECT_PAGE , List<String> dbFilmDouBanNoList);
}
