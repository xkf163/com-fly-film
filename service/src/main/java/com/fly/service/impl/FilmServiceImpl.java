package com.fly.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.util.QueryUtil;
import com.fly.common.utils.StrUtil;
import com.fly.dao.*;
import com.fly.entity.*;
import com.fly.service.FilmService;


import com.querydsl.core.types.Expression;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by xukangfeng on 2017/10/28 13:00
 */
@Service
public class FilmServiceImpl implements FilmService {

    @Autowired
    FilmRepository filmRepository;

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
        String sortInfo = queryCondition.getSortInfo();
        Sort sort = null;
        if (!StrUtil.isEmpty(sortInfo)) {
            System.out.println("------------sortInfo--------------");
            System.out.println(sortInfo);
            //判断排序类型及排序字段
            String[] sortArray = sortInfo.split(" ");
            System.out.println(sortArray);
            sort = "asc".equals(sortArray[1]) ? new Sort(Sort.Direction.ASC, sortArray[0]) : new Sort(Sort.Direction.DESC, sortArray[0]);
        }

        //4)dsl动态查询
        System.out.println("----------------------------查询条件");
        List<Map<String, Object>> conditions = queryCondition.getConditions();
        String subjectMain = null;
        Short year = null;
        if (!"".equals(conditions.get(0).get("value"))){
            subjectMain = (String) conditions.get(0).get("value");
        }
        if (!"".equals(conditions.get(1).get("value"))){
            year = isNumeric(conditions.get(1).get("value").toString()) ?  Short.parseShort(conditions.get(1).get("value").toString()) : 9999;
        }
        QFilm film = QFilm.film;
        //初始化组装条件(类似where 1=1)
        Predicate predicate = film.isNotNull().or(film.isNull());
        //执行动态条件拼装
        predicate = subjectMain == null ? predicate : ExpressionUtils.and(predicate,film.subjectMain.like(subjectMain));
        predicate = year == null ? predicate : ExpressionUtils.and(predicate,film.year.eq(year));

        for (Map<String, Object> conditionsMap : conditions) {
            String key = conditionsMap.get("key").toString();
            Object value = conditionsMap.get("value");
            System.out.println("key:"+key +" val:"+value);
        }

        Pageable pageable = new PageRequest(pageNum-1, pageSize , sort);
        Page<Film> pageCarrier = filmRepository.findAll(predicate , pageable);

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;

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
