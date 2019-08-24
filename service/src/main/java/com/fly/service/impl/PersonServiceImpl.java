package com.fly.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.util.QueryUtil;
import com.fly.common.utils.StrUtil;
import com.fly.dao.PersonRepository;
import com.fly.entity.*;
import com.fly.service.PersonService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:34 2017/10/30
 */
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    PersonRepository personRepository;

    @PersistenceContext
    EntityManager entityManager;



    @Override
    public Map<String, Object> findAll(String reqObj) throws Exception {
        //用于接收返回数据(配置、分页、数据)
        Map<String, Object> map = new HashMap<>();
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);

        // 分页信息
        PageInfo pageInfo = QueryUtil.getPageInfo(queryCondition);
        //获取Query配置
        Query query = QueryUtil.getQuery(queryCondition);

        int pageNum = pageInfo.getPageNum();
        int pageSize = pageInfo.getPageSize();

        //排序信息
        String sortInfo = !StrUtil.isEmpty(queryCondition.getSortInfo()) ? queryCondition.getSortInfo() : query.getOrder();
        Sort sort = null;
        if (!StrUtil.isEmpty(sortInfo)) {
            //判断排序类型及排序字段
            String[] sortArray = sortInfo.split(" ");
            System.out.println(sortArray);
            sort = "asc".equals(sortArray[1]) ? new Sort(Sort.Direction.ASC, sortArray[0]) : new Sort(Sort.Direction.DESC, sortArray[0]);
        }


        Pageable pageable = new PageRequest(pageNum-1, pageSize, sort);
        Page<Person> pageCarrier = personRepository.findAll( pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }



}
