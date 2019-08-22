package com.fly.common.query.controller;

import com.fly.common.query.service.QueryService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

import java.util.Map;

/**
 * 基于xml配置的query 需要优化
 *
 * @author billjiang
 */
@Controller
@RequestMapping("/query")
public class QueryController {


    @Resource
    private QueryService queryService;

    /**
     * 第一次加载页面初始化
     *
     * @param reqObj 前台参数
     * @return
     */
    @RequestMapping("/loadData")
    @ResponseBody
    public Map<String, Object> loadData(String reqObj) throws Exception {
        return queryService.loadData(reqObj);
    }



}
