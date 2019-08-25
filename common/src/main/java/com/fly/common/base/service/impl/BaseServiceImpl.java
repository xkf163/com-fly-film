package com.fly.common.base.service.impl;

import com.fly.common.base.service.BaseService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.Serializable;

@Service
public class BaseServiceImpl implements BaseService {


    @Override
    public <T> Serializable save(T obj) {
        return null;
    }
}
