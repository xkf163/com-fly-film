package com.fly.entity;
import lombok.Data;
import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by F on 2017/6/27.
 * 原始电影资料
 */
@Entity
@Table(name = "fm_film",uniqueConstraints = {@UniqueConstraint(name = "cons_film",columnNames = {"doubanNo","imdbNo","subject"})},indexes ={@Index(name = "index_film",columnList = "subject,doubanNo")})
@Data
public class Film implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String subject;

    private String subjectMain;

    private String subjectOther;

    private Short year;

    @Column(columnDefinition = "TEXT")
    private String info;

    @Column(columnDefinition = "TEXT")
    private String introduce;

    private String doubanNo;

    private Float doubanRating;

    private Long doubanSum;

    //评分
    private Float imdbRating;

    //评分人数
    private Long imdbSum;

    private String directors;

    @Column(columnDefinition = "TEXT")
    private String actors;

    private String genre;

    private String initialReleaseDate;

    private String runtime;

    private String imdbNo;

    private String country;

    @Transient
    private String episodeNumber;

}
