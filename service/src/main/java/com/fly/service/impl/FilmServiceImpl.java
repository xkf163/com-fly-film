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


import com.fly.service.PersonService;
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
import java.util.*;


/**
 * Created by xukangfeng on 2017/10/28 13:00
 */
@Service
public class FilmServiceImpl implements FilmService {

    @Autowired
    FilmRepository filmRepository;

    @PersistenceContext
    EntityManager entityManager;


    @Autowired
    PersonService personService;

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
            //判断排序类型及排序字段
            String[] sortArray = sortInfo.split(" ");
            System.out.println(sortArray);
            sort = "asc".equals(sortArray[1]) ? new Sort(Sort.Direction.ASC, sortArray[0]) : new Sort(Sort.Direction.DESC, sortArray[0]);
        }

        //4)dsl动态查询
        List<Map<String, Object>> conditions = queryCondition.getConditions();
        String subjectMain = "";
        Short year = 9999;
        String PersonNo = "-1";
        if (!conditions.isEmpty()){
            for(int i = 0 ; i < conditions.size() ; i++) {
                System.out.println(conditions.get(i).get("key"));
                System.out.println(conditions.get(i).get("value"));
                if ("subjectMain".equals(conditions.get(i).get("key"))) {
                    subjectMain =  (String) conditions.get(i).get("value");
                    subjectMain = subjectMain.trim();
                }
                if ("year".equals(conditions.get(i).get("key"))) {
                    if (!"".equals(conditions.get(i).get("value").toString())){
                        year = Short.parseShort ( conditions.get(i).get("value").toString() );
                    }
                }
                if ("PersonNo".equals(conditions.get(i).get("key"))) {
                    PersonNo =  (String) conditions.get(i).get("value");
                    PersonNo = PersonNo.trim();
                }
            }
        }

        for (Map<String, Object> conditionsMap : conditions) {
            String key = conditionsMap.get("key").toString();
            Object value = conditionsMap.get("value");
            System.out.println("key:"+key +" val:"+value);
        }


        QFilm film = QFilm.film;
        //初始化组装条件(类似where 1=1)
        Predicate predicate = film.isNotNull().or(film.isNull());
        //执行动态条件拼装
        predicate =  "".equals(subjectMain) ? predicate : ExpressionUtils.and(predicate,film.subjectMain.like(subjectMain));
        predicate = year == 9999 ? predicate : ExpressionUtils.and(predicate,film.year.eq(year));
        predicate = "-1".equals(PersonNo) ? predicate : ExpressionUtils.and(predicate,film.directors.contains(PersonNo).or(film.screenWriter.contains(PersonNo)).or(film.actors.contains(PersonNo)));

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


            if (null == ff.getId()) {
                entityManager.persist(ff);
            }else{
                entityManager.merge(ff);
            }


            if (i % 10 == 0 || i == (size - 1)) { // 每10条数据执行一次，或者最后不足10条时执行
                entityManager.flush();
                entityManager.clear();
            }
        }
    }




    @Override
    public Map<String, Object> filmAllOfPerson(String reqObj) throws Exception {
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

        //4)dsl动态查询
        List<Map<String, Object>> conditions = queryCondition.getConditions();

        String subjectMain = null ,personId = "0", propName = null;
        if (!"".equals(conditions.get(0).get("value"))){
            personId = (String) conditions.get(0).get("value");
        }
        if (!"".equals(conditions.get(1).get("value"))){
            subjectMain =  (String) conditions.get(1).get("value");
        }
        if (!"".equals(conditions.get(2).get("value"))){
            propName =  (String) conditions.get(2).get("value");
        }

        System.out.println(personId);

        QFilm film = QFilm.film;

        Person person = personService.findOne(Long.valueOf(personId));
        Predicate predicate = film.id.stringValue().eq("-1");
        if (person != null){
            String personDoubanNo = person.getDouBanNo();
            personDoubanNo = "%"+personDoubanNo+"%";
            System.out.println(personDoubanNo);
            System.out.println(propName);
            if("directors".equals(propName)){
                predicate = film.directors.like(personDoubanNo);
            }else if("actors".equals(propName)){
                predicate = film.actors.like(personDoubanNo);
            }else{
                predicate = film.screenWriter.like(personDoubanNo);
            }
        }

        predicate = subjectMain == null ? predicate : ExpressionUtils.and(predicate,film.subjectMain.like(subjectMain));

        Page<Film> pageCarrier = filmRepository.findAll(predicate , pageable);
        List<Column> columnCarrier = query.getColumnList();

        map.put("pageCarrier", pageCarrier);
        map.put("columnCarrier", columnCarrier);

        return map;
    }




    @Override
    public Film findBySubjectAndDoubanNo(Film film) {
       // System.out.println(film);
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


    @Override
    public List<String> findAllUrlOfFilmWithoutLogo() {

        QFilm qFilm = QFilm.film;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> listDouBanNo = jpaQueryFactory.select(qFilm.doubanNo.prepend("https://movie.douban.com/subject/").append("/"))
                .from(qFilm)
                .where(qFilm.filmLogo.isNull().and(qFilm.doubanNo.isNotNull()))
                .fetch();

        return listDouBanNo;

    }


    @Override
    public List<String> findAllUrlOfMediaFilmWithoutLogo() {
        QMedia qMedia = QMedia.media;
        //QFilm qFilm = QFilm.film;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> listDouBanNo = jpaQueryFactory.select(qMedia.film.doubanNo.prepend("https://movie.douban.com/subject/").append("/"))
                .from(qMedia)
                .where(qMedia.film.isNotNull().and(qMedia.film.filmLogo.isNull()).and(qMedia.film.doubanNo.isNotNull()))
                .where(qMedia.deleted.ne(1))
                .orderBy(qMedia.gatherDate.desc())
                .fetch();

        return listDouBanNo;
    }

    @Override
    public Film findByDoubanNo(String doubanNo) {
        return filmRepository.findByDoubanNo(doubanNo);
    }

    @Override
    public Film findOne(Long id) {
        return filmRepository.findOne(id);
    }


    /**
     * 根据 人物豆瓣编号 查找其 所有参与作品
     * @param
     * @return
     */
    @Override
    public List<String> findAllOfPerson(String name) {
        Person person = personService.findByNameContaining(name);
        if (null == person){
            //认为是豆瓣编号 再进行搜索一次
            person = personService.findByDouBanNo(name);
            if (null == person){
                return new ArrayList<>();
            }
        }
        String douBanNo = person.getDouBanNo();
        QFilm qFilm = QFilm.film;
        JPAQueryFactory jpaQueryFactory = new JPAQueryFactory(entityManager);
        List<String> lists = jpaQueryFactory.select(qFilm.doubanNo.prepend("https://movie.douban.com/subject/").append("/"))
                .from(qFilm)
                .where(qFilm.filmLogo.isNull().and(qFilm.doubanNo.isNotNull()))
                .where(qFilm.directors.contains(douBanNo).or(qFilm.screenWriter.contains(douBanNo)).or(qFilm.actors.contains(douBanNo)))
                .fetch();
        return lists;
    }

    @Override
    public Film findBySubjectContaining(String subject) {
        return filmRepository.findBySubjectContaining(subject);
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
