package com.fly.common.base.pojo;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author:xukangfeng
 * @Description 统一结果返回类
 * @Date : Create in 9:43 2017/11/1
 */
@Data
public class ResultBean<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int NO_LOGIN = -1;

    public static final int SUCCESS = 0;

    public static final int FAIL = 1;

    public static final int NO_PERMISSION = 2;

    private String msg = "success";

    private int code = SUCCESS;

    private T data;

    public ResultBean() {
        super();
    }

    public ResultBean(T data) {
        super();
        this.data = data;
    }

    public ResultBean(Throwable e) {
        super();
        this.msg = e.toString();
        this.code = FAIL;
    }
}
