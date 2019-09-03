package com.fly.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.util.QueryUtil;
import com.fly.common.utils.StrUtil;
import com.fly.dao.MediaRepository;
import com.fly.entity.Film;
import com.fly.entity.Media;
import com.fly.entity.QFilm;
import com.fly.entity.QMedia;
import com.fly.service.MediaService;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Order;
import com.querydsl.core.types.OrderSpecifier;
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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:28 2019/8/22
 */
@Service
public class MediaServiceImpl implements MediaService {

    @Autowired
    MediaRepository mediaRepository;

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
        String nameChn = null;
        Short year = null;
        if (!"".equals(conditions.get(0).get("value"))){
            nameChn = (String) conditions.get(0).get("value");
        }
        if (!"".equals(conditions.get(1).get("value"))){
            year = isNumeric(conditions.get(1).get("value").toString()) ?   Short.parseShort(conditions.get(1).get("value").toString()) : 9999;
        }

        QMedia media = QMedia.media;
        //初始化组装条件(类似where 1=1)
        Predicate predicate = media.isNotNull().or(media.isNull());
        //执行动态条件拼装
        predicate = nameChn == null ? predicate : ExpressionUtils.and(predicate,media.nameChn.like(nameChn));
        predicate = year == null ? predicate : ExpressionUtils.and(predicate,media.year.eq(year));



        Pageable pageable = new PageRequest(pageNum-1, pageSize, sort);
        Page<Media> pageCarrier = mediaRepository.findAll(predicate , pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }


    /**
     * 去重
     * @param reqObj
     * @return
     */
    @Override
    public Map<String, Object> findDuplicate(String reqObj) throws Exception {

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
        String sortInfo = "nameChn asc";
        Sort sort = null;
        if (!StrUtil.isEmpty(sortInfo)) {
            //判断排序类型及排序字段
            String[] sortArray = sortInfo.split(" ");
            System.out.println(sortArray);
            sort = "asc".equals(sortArray[1]) ? new Sort(Sort.Direction.ASC, sortArray[0]) : new Sort(Sort.Direction.DESC, sortArray[0]);
        }

        Pageable pageable = new PageRequest(pageNum-1, pageSize, sort);

        QMedia media = QMedia.media;
        //查询语句动态准备
        List<Predicate> criteria = new ArrayList<>();
        criteria.add(media.deleted.eq(0));



        JPAQueryFactory queryFactory = new JPAQueryFactory(entityManager);
        List<String> listRepeat = queryFactory.selectFrom(media)
                .groupBy(media.nameChn,media.year)
                .select(media.nameChn)
                .where(criteria.toArray(new Predicate[criteria.size()]))
                .having(media.nameChn.count().gt(1))
                .fetch();
        //再次搜索：带分页
        Predicate predicate = media.nameChn.in(listRepeat).and(media.deleted.eq(0));

        Page<Media> pageCarrier = mediaRepository.findAll(predicate , pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }



    @Override
    public Map<String, Object> findUnlink(String reqObj) throws Exception {
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
        //String sortInfo = "gatherDate desc";
        Sort sort = null;
        if (!StrUtil.isEmpty(sortInfo)) {
            //判断排序类型及排序字段
            String[] sortArray = sortInfo.split(" ");
            //System.out.println(sortArray);
            sort = "asc".equals(sortArray[1]) ? new Sort(Sort.Direction.ASC, sortArray[0]) : new Sort(Sort.Direction.DESC, sortArray[0]);
        }


        Pageable pageable = new PageRequest(pageNum-1, pageSize, sort);

        QMedia media = QMedia.media;
        //再次搜索：带分页
        Predicate predicate = media.film.isNull().and(media.deleted.eq(0));

        Page<Media> pageCarrier = mediaRepository.findAll(predicate , pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }


    @Override
    public Media findOne(Long id) {
        return mediaRepository.findOne(id);
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
