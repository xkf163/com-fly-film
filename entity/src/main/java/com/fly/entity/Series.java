package com.fly.entity;


import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import javax.persistence.*;
import java.util.Date;

/**
 * 影音系列
 */
@Entity
@Data
@Table(name = "fm_series")
public class Series {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String memo;

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date createDate;    //条目创建时间

    @JsonFormat(locale="zh", timezone="GMT+8", pattern="yyyy-MM-dd HH:mm:ss")
    private Date modifiedDate;  //资源修改时间

    private String asMedias;

    private Integer asMediaNumber = 0;


}
