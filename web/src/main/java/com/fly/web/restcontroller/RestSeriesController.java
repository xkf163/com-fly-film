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


    @PostMapping(value = "/update")
    private ResultBean<Series> updateSeries(String medias, String mediasLength, String seriesId) {

        if ("".equals(seriesId)){
            return new ResultBean<>();
        }

        Series series = seriesService.findOne(Long.parseLong(seriesId));
        if (series ==  null){
            return new ResultBean<>();
        }


        series.setUpdateDate(new Date());
        if (series.getAsMedias() == null){
            series.setAsMedias(medias);
            series.setAsMediaNumber(Integer.parseInt(mediasLength));
        }else {
            medias = series.getAsMedias()+","+medias;
            String[] a = medias.split(",");
            series.setAsMedias(medias);
            series.setAsMediaNumber(a.length);
        }

        seriesService.save(series);
        return new ResultBean<>(series);
    }

}