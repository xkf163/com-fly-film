package com.fly.dao;

import com.fly.entity.Star;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 8:48 2017/11/2
 */

@Repository
public interface StarRepository extends JpaRepository<Star,Long>,QueryDslPredicateExecutor<Star> {

    Star findByDouBanNo(String douBanNo);

}
