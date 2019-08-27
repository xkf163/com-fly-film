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

}
