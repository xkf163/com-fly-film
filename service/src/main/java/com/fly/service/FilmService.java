package com.fly.service;


import com.fly.entity.Film;

import java.util.List;
import java.util.Map;

/**
 * Created by xukangfeng on 2017/10/28 12:59
 */
public interface FilmService {

    Map<String, Object> findAll(String reqObj) throws Exception;

    void batchInsertAndUpdate(List<Film> films);

    Film findBySubjectAndDoubanNo(Film film);

    Film findByDoubanNo(String doubanNo);

    Map<String, Object> filmAllOfPerson(String reqObj) throws Exception ;

    void save(Film film);

    List<String> findAllDouBanNo();

    List<String> findAllUrlOfFilmWithoutLogo() ;

    List<String> findAllUrlOfMediaFilmWithoutLogo() ;

    Film findOne(Long id);

    List<String> findAllOfPerson(String name);

    Film findBySubjectContaining(String subject);

}
