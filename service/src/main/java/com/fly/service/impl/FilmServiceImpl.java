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


import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.math.BigDecimal;
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

    @PersistenceContext
    EntityManager entityManager;


    /*
    Datatables 返回所有Film列表
     */
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


    @Override
    @Transactional
    public void batchInsertAndUpdate(List<Film> films) {
        int size = films.size();
        for (int i = 0; i < size; i++) {
            Film ff = films.get(i);

            Film film = findBySubjectAndDoubanNo(ff);
            if (null == film) {
                entityManager.persist(ff);
            }

            if (i % 10 == 0 || i == (size - 1)) { // 每10条数据执行一次，或者最后不足10条时执行
                entityManager.flush();
                entityManager.clear();
            }
        }
    }



    @Override
    public Film findBySubjectAndDoubanNo(Film film) {
        System.out.println(film);
        //判断数据库里是否存在
        QFilm qFilm = QFilm.film;
        Predicate predicate = qFilm.subject.eq(film.getSubject()).and(qFilm.doubanNo.eq(film.getDoubanNo()));
        return filmRepository.findOne(predicate);
    }


    @Override
    public void save(Film film) {
        filmRepository.save(film);
    }


    @Override
    public List<String> findAllDouBanNo() {

            QFilm qFilm = QFilm.film;
            JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
            List<String> listDouBanNo = jpaQueryFactory.select(qFilm.doubanNo)
                    .from(qFilm)
                    .fetch();
            return listDouBanNo;

    }



    /*
        是否未数字
         */
    public static boolean isNumeric(String str) {
        String bigStr;
        try {
            bigStr = new BigDecimal(str).toString();
        } catch (Exception e) {
            return false; //异常 说明包含非数字。
        }
        return true;
    }

}
