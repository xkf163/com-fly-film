package com.fly.web.restcontroller;

import com.fly.common.base.pojo.Result;
import com.fly.common.base.pojo.ResultBean;
import com.fly.common.utils.StrUtil;
import com.fly.entity.Media;
import com.fly.entity.Series;
import com.fly.service.SeriesService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

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


    @PostMapping(value = "/reduce")
    private ResultBean<Series> reduceSeries(String medias, String mediasLength, String seriesId) {
        if ("".equals(seriesId)){
            return new ResultBean<>();
        }

        Series series = seriesService.findOne(Long.parseLong(seriesId));
        if (series ==  null){
            return new ResultBean<>();
        }

        String oldMedias = series.getAsMedias();
        String[] oldMediasArray = oldMedias.split(",");
        List<String> oldMediasList = new ArrayList<String>(Arrays.asList(oldMediasArray));

        String[] removeMediaArray = medias.split(",");
        List<String> removeMediasList = new ArrayList<String>(Arrays.asList(removeMediaArray));

        for ( int i = 0; i < oldMediasList.size(); i++) {
            String om = oldMediasList.get(i);
            if (removeMediasList.contains(om)) {
                oldMediasList.remove(om);  // ok
                i--; // 因为位置发生改变，所以必须修改i的位置
            }
        }

        series.setAsMediaNumber(oldMediasList.size());
        series.setAsMedias(StringUtils.join(oldMediasList.toArray(), ","));
        series.setUpdateDate(new Date());

        seriesService.save(series);

        return new ResultBean<>(series);
    }


}