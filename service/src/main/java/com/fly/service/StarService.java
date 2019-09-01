package com.fly.service;

import com.fly.entity.Star;

import java.util.List;
import java.util.Map;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 8:48 2017/11/2
 */
public interface StarService {
    Map<String, Object> findAll(String reqObj) throws Exception;


    List<String> findAllDouBanNo();

    Star findByDouBanNo(String douBanNo);

    Star save(Star star);
}
