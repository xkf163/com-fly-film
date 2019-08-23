package com.fly.service;

import com.fly.entity.Film;
import org.springframework.data.domain.Page;


import java.util.List;

/**
 * Created by xukangfeng on 2017/10/28 12:59
 */
public interface FilmService {

    Page<Film> findAll(String page,String size,String sort,String order);



}
