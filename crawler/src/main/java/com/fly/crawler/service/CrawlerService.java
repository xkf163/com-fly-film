package com.fly.crawler.service;


import com.fly.crawler.entity.Crawler;
import com.fly.entity.Film;
import us.codecraft.webmagic.Page;

import java.util.List;

public interface CrawlerService {

    void running(Crawler crawler);

    Film extractFilm(Page page);

    void saveFilmList(List<Film> filmList);

}
