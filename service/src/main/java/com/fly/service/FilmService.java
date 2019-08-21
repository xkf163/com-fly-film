package com.fly.service;

import com.fly.entity.Film;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * Created by xukangfeng on 2017/10/28 12:59
 */
public interface FilmService {

    //从网页中提取Film Object
    Film extractFilmSecondFromCrawler(Page page, Film film);

    Film extractFilmFirstFromCrawler(Page page);

    Film findBySubjectAndDoubanNo(Film film);

    void save(Film film);

    //根据几个不为空字段来判断film是否需要保存，false直接跳过该电影后续抓取
    String[] needCrawler(Film film, String directrAllowEmpty, String actorAllowEmpty);

    Object[] connectFilmForMedia(String onlyNone);

    List<Film> listFilmsByStarId(String starId, String type);

    List<String> listFilmsDouBanNo();

    //所有数据库中未关联film的media。name
    List<String> listFilmsNameUnconnect();
}
