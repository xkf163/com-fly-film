package com.fly.common.base.service;

import java.io.Serializable;

public interface BaseService {

    /**
     * 保存对象
     *
     * @param obj 所要保存的对象
     * @return 唯一主键
     */
    <T> Serializable save(T obj);


}
