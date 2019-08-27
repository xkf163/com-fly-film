package com.fly.crawler.service;


import com.fly.crawler.entity.Crawler;
import com.fly.entity.Film;
import us.codecraft.webmagic.Page;

public interface CrawlerService {

    void running(Crawler crawler);

    Film extractFilm(Page page);

}
