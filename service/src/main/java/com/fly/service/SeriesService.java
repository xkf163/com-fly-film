package com.fly.service;


import com.fly.common.base.service.BaseService;
import com.fly.entity.Series;

import java.util.Map;

/**
 * Created by xukangfeng on 2017/10/28 12:59
 */
public interface SeriesService {
    Map<String, Object> findAll(String reqObj) throws Exception;
    void save(Series series);
}
