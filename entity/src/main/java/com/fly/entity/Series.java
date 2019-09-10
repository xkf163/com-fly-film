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
public class Series extends BaseEntity {

    @Id
    @GeneratedValue
    private Long id;

    private String name;

    private String memo;


    private String asMedias;

    private Integer asMediaNumber = 0;


}
