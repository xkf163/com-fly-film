package com.fly.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.util.QueryUtil;
import com.fly.common.utils.StrUtil;

import com.fly.dao.SeriesRepository;
import com.fly.entity.QSeries;
import com.fly.entity.Series;
import com.fly.service.SeriesService;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xukangfeng on 2017/10/28 13:00
 */
@Service
public class SeriesServiceImpl implements SeriesService {

    @Autowired
    SeriesRepository seriesRepository;

    @Override
    public Map<String, Object> findAll(String reqObj) throws Exception{

        //用于接收返回数据(配置、分页、数据)
        Map<String, Object> map = new HashMap<>();
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);

        // 1)分页信息
        PageInfo pageInfo = QueryUtil.getPageInfo(queryCondition);
        //2)获取Query配置
        Query query = QueryUtil.getQuery(queryCondition);
        //获取表头
        List<Column> columnCarrier = query.getColumnList();

        Integer pageNum = pageInfo.getPageNum();
        Integer pageSize = pageInfo.getPageSize();

        //3)排序信息
        String sortInfo = !StrUtil.isEmpty(queryCondition.getSortInfo()) ? queryCondition.getSortInfo() : query.getOrder();
        Sort sort = null;
        if (!StrUtil.isEmpty(sortInfo)) {
            System.out.println("------------sortInfo--------------");
            System.out.println(sortInfo);
            //判断排序类型及排序字段
            String[] sortArray = sortInfo.split(" ");
            System.out.println(sortArray);
            sort = "asc".equals(sortArray[1]) ? new Sort(Sort.Direction.ASC, sortArray[0]) : new Sort(Sort.Direction.DESC, sortArray[0]);
        }



        QSeries series = QSeries.series;
        //初始化组装条件(类似where 1=1)
        Predicate predicate = series.isNotNull().or(series.isNull());
        //执行动态条件拼装
        //predicate = subjectMain == null ? predicate : ExpressionUtils.and(predicate,film.subjectMain.like(subjectMain));
        //predicate = year == null ? predicate : ExpressionUtils.and(predicate,film.year.eq(year));

        Pageable pageable = new PageRequest(pageNum-1, pageSize , sort);

        Page<Series> pageCarrier = seriesRepository.findAll(pageable);

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;

    }


    @Override
    public Series findOne(Long id) {
        return seriesRepository.findOne(id);
    }

    @Override
    public void save(Series series) {
        seriesRepository.save(series);
    }

    public static boolean isNumeric(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false;//异常 说明包含非数字。
        }
        return true;
    }



}
