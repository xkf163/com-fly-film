package com.fly.common.query.service;

import java.util.Map;

public interface QueryService  {

    /**
     * 通用加载数据方法
     *
     * @param reqObj 前台请求参数
     * @return
     */
    Map<String, Object> loadData(String reqObj) throws Exception;

}
