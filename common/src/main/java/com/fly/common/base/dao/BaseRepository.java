package com.fly.common.base.dao;

import java.io.Serializable;

public interface BaseRepository {
    /**
     * 保存对象
     *
     * @param obj 所要保存的对象
     * @return 唯一主键
     */
    <T> Serializable save(T obj);
}
