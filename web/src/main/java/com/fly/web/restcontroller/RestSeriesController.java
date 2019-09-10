package com.fly.web.restcontroller;

import com.fly.common.base.pojo.Result;
import com.fly.common.base.pojo.ResultBean;
import com.fly.common.utils.StrUtil;
import com.fly.entity.Media;
import com.fly.entity.Series;
import com.fly.service.SeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

/**
 *
 */
@RestController
@RequestMapping(value = "/api/series")
public class RestSeriesController {

    @Autowired
    SeriesService seriesService;

    @PostMapping(value = "/all")
    public Map<String, Object> seriesAll(String reqObj) throws Exception {
        return seriesService.findAll(reqObj);
    }

    @PostMapping(value = "/save")
    private ResultBean<Series> saveSeries(Series series) {

        if (series.getId() == null){
            series.setCreateDate(new Date());
        }
        series.setUpdateDate(new Date());
        seriesService.save(series);
        return new ResultBean<>(series);
    }

    @PostMapping(value = "/get")
    private Series getSeries(String id) {
        return seriesService.findOne(Long.parseLong(id));
    }




}