package com.fly.service;

import com.fly.entity.Media;

import java.util.Map;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:28 2019/8/22
 */
public interface MediaService {
    Map<String, Object> findAll(String reqObj) throws Exception;

    Map<String, Object> findDuplicate(String reqObj) throws Exception;

    Map<String, Object> findUnlink(String reqObj) throws Exception;

    Media findOne(Long id);


}
