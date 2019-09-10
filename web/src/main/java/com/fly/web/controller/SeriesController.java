package com.fly.web.controller;

import com.fly.common.base.pojo.Result;
import com.fly.entity.Series;
import com.fly.service.SeriesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fly.common.utils.StrUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:26 2019/8/22
 */

@Controller
@RequestMapping(value = "/series")
public class SeriesController {


    @Autowired
    SeriesService seriesService;

    @GetMapping(value = "/all")
    public String seriesAll(HttpServletRequest request) {
        request.setAttribute("dataSeriesUrl", "api/series/all");
        request.setAttribute("dataMediaUrl", "api/media/series");
        return "views/series/series_list";
    }


    /**
     * 系列编辑
     *
     * @return
     */
    @GetMapping(value = "/edit")
    private String edit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/series/edit";
    }







    @RequestMapping(method = RequestMethod.POST, value = "/get")
    @ResponseBody
    private Series getSeries(Long id) {
       // return seriesService.get(Series.class, id);
        return null;
    }

}
