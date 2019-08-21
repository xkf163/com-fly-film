package com.fly.service.impl;

import com.fly.dao.StarRepository;
import com.fly.entity.Star;
import com.fly.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 8:48 2017/11/2
 */
@Service
public class StarServiceImpl implements StarService {

    @Autowired
    StarRepository starRepository;

    @Override
    public List<Star> findAll() {
        return starRepository.findAll();
    }

    @Override
    public Star findByDouBanNo(String douBanNo){
        return starRepository.findByDouBanNo(douBanNo);
    }

    @Override
    public Star findById(Long id){
        return starRepository.findById(id);
    }
}
