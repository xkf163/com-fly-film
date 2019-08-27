package com.fly.crawler.service;


import com.fly.entity.Film;
import us.codecraft.webmagic.Page;

public interface CrawlerService {

    void running();

    Film extractFilm(Page page);

}
