package com.fly.dao;

import com.fly.entity.Film;
import com.fly.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by F on 2017/6/27.
 */
@Repository
public interface SeriesRepository extends JpaRepository<Series,Long>, QueryDslPredicateExecutor<Series> {
}
