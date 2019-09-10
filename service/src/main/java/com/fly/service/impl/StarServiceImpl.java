package com.fly.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.util.QueryUtil;
import com.fly.common.utils.StrUtil;
import com.fly.dao.StarRepository;
import com.fly.entity.Person;
import com.fly.entity.QFilm;
import com.fly.entity.QStar;
import com.fly.entity.Star;
import com.fly.service.StarService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
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
 * @Date : Create in 8:48 2017/11/2
 */
@Service
public class StarServiceImpl implements StarService {

    @Autowired
    StarRepository starRepository;


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


        //4)dsl动态查询
        List<Map<String, Object>> conditions = queryCondition.getConditions();
        String name = null ;
        if (!"".equals(conditions.get(0).get("value"))){
            name =  (String) conditions.get(0).get("value");
        }

        QStar star = QStar.star;
        Predicate predicate = star.deleted.eq(0);
        predicate = name == null ? predicate : ExpressionUtils.and(predicate,star.name.like(name));

        Pageable pageable = new PageRequest(pageNum-1, pageSize, sort);
        Page<Star> pageCarrier = starRepository.findAll(predicate , pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }


    @Override
    public List<String> findAllDouBanNo() {

        QStar star = QStar.star;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> listDouBanNo = jpaQueryFactory.select(star.douBanNo)
                .from(star)
                .fetch();
        return listDouBanNo;

    }

    @Override
    public Star findOne(Long id) {
        return starRepository.findOne(id);
    }

    @Override
    public Star findByDouBanNo(String douBanNo) {
        return starRepository.findByDouBanNo(douBanNo);
    }

    @Override
    public Star save(Star star) {
        return starRepository.save(star);
    }
}
