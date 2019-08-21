package com.fly.service;

import com.fly.entity.Person;
import us.codecraft.webmagic.Page;

import java.util.List;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:33 2017/10/30
 */
public interface PersonService {

    //从网页中提取Film Object
    Person extractFilmSecondFromCrawler(Page page);

    Person extractFilmFirstFromCrawler(Page page);

    Person findByNameAndDoubanNO(Person person);

    void save(Person person);

    //根据不为空字段来判断是否需要保存到数据库
    boolean needCrawler(Person person);

    Person findByDoubanNo(String doubanNo);

    List<String> listPersonsDouBanNo();

    Person findById(Long id);
}
