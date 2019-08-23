package com.fly.service.impl;

import com.fly.dao.*;
import com.fly.entity.*;
import com.fly.service.FilmService;

import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;


/**
 * Created by xukangfeng on 2017/10/28 13:00
 */
@Service
public class FilmServiceImpl implements FilmService {

    @Autowired
    FilmRepository filmRepository;

    @Override
    public Page<Film> findAll(String page,String size,String sort,String order) {

        //排序及分页
        Sort querySort = new Sort(Sort.Direction.ASC,sort);
        if("DESC".equals(order.toUpperCase()))
            querySort = new Sort(Sort.Direction.DESC,sort);
        Pageable pageable = new PageRequest(Integer.parseInt(page)-1, Integer.parseInt(size), querySort);
        return filmRepository.findAll(pageable);

    }


}
