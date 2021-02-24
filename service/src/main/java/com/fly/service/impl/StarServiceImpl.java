package com.fly.service.impl;

import com.alibaba.fastjson.JSON;
import com.fly.common.base.pojo.PageInfo;
import com.fly.common.query.entity.Column;
import com.fly.common.query.entity.Query;
import com.fly.common.query.entity.QueryCondition;
import com.fly.common.query.util.QueryUtil;
import com.fly.common.utils.StrUtil;
import com.fly.dao.StarRepository;
import com.fly.entity.*;
import com.fly.service.MediaService;
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
import java.util.ArrayList;
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

    @Autowired
    MediaService mediaService;

    @PersistenceContext
    EntityManager entityManager;

    @Override
    public Map<String, Object> findAll(String reqObj) throws Exception {

        System.out.println(reqObj);
        //用于接收返回数据(配置、分页、数据)
        Map<String, Object> map = new HashMap<>();
        QueryCondition queryCondition = JSON.parseObject(reqObj, QueryCondition.class);
        //获取Query配置
        Query query = QueryUtil.getQuery(queryCondition);
        // 分页信息
        PageInfo pageInfo = QueryUtil.getPageInfo(queryCondition);
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
        String name = "";
        String mediaId = "-1";
//        if (!conditions.isEmpty() && !"".equals(conditions.get(0).get("value"))){
//            name =  (String) conditions.get(0).get("value");
//        }
        if (!conditions.isEmpty()){
            for(int i = 0 ; i < conditions.size() ; i++) {
//                System.out.println(conditions.get(i).get("key"));
//                System.out.println(conditions.get(i).get("value"));
                if ("name".equals(conditions.get(i).get("key"))) {
                    name =  (String) conditions.get(i).get("value");
                }
                if ("MediaId".equals(conditions.get(i).get("key"))) {
                    mediaId =  (String) conditions.get(i).get("value");

                }
            }
        }
 //       System.out.println("mediaId: " + mediaId);
//        System.out.println(String.valueOf(new String[]{mediaId, mediaId+","}));
        String starAsDirect="",starAsActor="",starAsWriter="";
        if(!"-1".equals(mediaId)){
            Media media = mediaService.findOne(Long.valueOf(mediaId));

            starAsDirect = media.getDirector();
            starAsActor= media.getActor();
            starAsWriter = media.getWriter();

            starAsDirect = starAsDirect == null ? "" : starAsDirect;
            starAsActor = starAsActor == null ? "" : starAsActor;
            starAsWriter = starAsWriter == null ? "" : starAsWriter;
        }
        List<Long> b = new ArrayList<Long>();
        if (!"".equals(starAsDirect)){
            for (String retval: starAsDirect.split(",")){
                System.out.println(retval);
                Long a = Long.valueOf(retval);
                b.add(a);
            }
        }
        if (!"".equals(starAsActor)){
            for (String retval: starAsActor.split(",")){
                System.out.println(retval);
                Long a = Long.valueOf(retval);
                b.add(a);
            }
        }
        if (!"".equals(starAsWriter)){
            for (String retval: starAsWriter.split(",")){
                System.out.println(retval);
                Long a = Long.valueOf(retval);
                b.add(a);
            }
        }
        Long[] c = new Long[b.size()];
        b.toArray(c);

        QStar star = QStar.star;
        Predicate predicate = star.deleted.eq(0);
        //predicate = "-1".equals(mediaId) ? predicate : ExpressionUtils.and(predicate,star.asActor.contains(mediaId).or(star.asDirector.contains(mediaId)).or(star.asWriter.contains(mediaId)));
        predicate = "".equals(name) ? predicate : ExpressionUtils.and(predicate,star.name.like(name));
        predicate = "-1".equals(mediaId) ? predicate : ExpressionUtils.and(predicate,star.id.in(c));

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
