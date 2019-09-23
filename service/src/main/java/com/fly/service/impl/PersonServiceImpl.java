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
import org.springframework.transaction.annotation.Transactional;


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


        //4)dsl动态查询
        List<Map<String, Object>> conditions = queryCondition.getConditions();
        String nameExtend = null;
        if (conditions!=null && conditions.size()>-1){
            if (!"".equals(conditions.get(0).get("value"))){
                nameExtend = (String) conditions.get(0).get("value");
            }
        }

        QPerson person = QPerson.person;
        Predicate predicate = person.isNotNull().or(person.isNull());
        predicate = nameExtend == null ? predicate : ExpressionUtils.and(predicate,person.nameExtend.like(nameExtend));

        Pageable pageable = new PageRequest(pageNum-1, pageSize, sort);
        Page<Person> pageCarrier = personRepository.findAll( predicate ,pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }


    @Override
    public List<String> findAllDouBanNo() {
        QPerson person = QPerson.person;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> listDouBanNo = jpaQueryFactory.select(person.douBanNo)
                .from(person)
                .fetch();
        return listDouBanNo;
    }


    @Override
    public List<String> findImportWithoutLogoList() {
        QStar qStar = QStar.star;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> listDouBanNo = jpaQueryFactory.select(qStar.person.douBanNo.prepend("https://movie.douban.com/celebrity/").append("/"))
                .from(qStar)
                .where(qStar.person.faceLogo.isNull().and(qStar.person.douBanNo.isNotNull()))
                .where(qStar.asActorNumber.gt(2).or(qStar.asDirectorNumber.gt(5)))
                .fetch();
        return listDouBanNo;
    }


    @Override
    @Transactional
    public void batchInsertAndUpdate(List<Person> persons) {
        int size = persons.size();
        for (int i = 0; i < size; i++) {
            Person pp = persons.get(i);

            if (null == pp.getId()) {
                entityManager.persist(pp);
            }else{
                entityManager.merge(pp);
            }

            if (i % 10 == 0 || i == (size - 1)) { // 每10条数据执行一次，或者最后不足10条时执行
                entityManager.flush();
                entityManager.clear();
            }
        }
    }

    @Override
    public Person findOne(Long id) {
        return personRepository.findOne(id);
    }

    @Override
    public Person findByDouBanNo(String douBanNo) {
        return personRepository.findByDouBanNo(douBanNo);
    }

    @Override
    public void save(Person person) {
        personRepository.save(person);
    }

    @Override
    public Map getPersonNamesByPersonIds(String personIds) {
        String[] strArr = personIds.split(",");
        Long[] retArr = new Long[strArr.length];
        for (int i = 0; i < strArr.length; i++) {
            retArr[i] = Long.parseLong(strArr[i].trim());
        }

        QPerson qPerson = QPerson.person;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> listPersonName = jpaQueryFactory.select(qPerson.name)
                .from(qPerson)
                .where(qPerson.id.in(retArr))
                .where(qPerson.deleted.ne(1))
                .fetch();

        Map map = new HashMap();
        map.put("name", StringUtils.join(listPersonName.toArray(), ","));
        return map;
    }
}
