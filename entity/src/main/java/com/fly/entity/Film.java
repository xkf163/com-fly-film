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
public class Film extends BaseEntity implements Serializable{

    @Id
    @GeneratedValue
    private Long id;

    private String subject;

    private String subjectMain;

    //又名
    private String subjectOther;

    //年代
    private Short year;

    //html 源码
    //@Column(columnDefinition = "TEXT")
    //private String info;

    //影片简介
    @Column(columnDefinition = "TEXT")
    private String introduce;

    //豆瓣编号
    private String doubanNo;

    //豆瓣评分
    private Float doubanRating;

    //豆瓣评分人数
    private Long doubanSum;

    //IMDB编号
    private String imdbNo;

    //IMDB评分
    private Float imdbRating;

    //IMDB评分人数
    private Long imdbSum;

    //导演们 的id字符串，用逗号分隔
    private String directors;

    //演员们的id字符串，用逗号分隔
    @Column(columnDefinition = "TEXT")
    private String actors;

    //编剧们  的id字符串，用逗号分隔
    private String screenWriter ;

    //影片类型
    private String genre;

    //影片发行日期
    private String initialReleaseDate;

    //时长
    private Short runtime;

    //时长文本（包括单位 播放地区）
    //private String runtimeFull;

    //国家或地区
    private String country;

    //电视剧集数
    @Transient
    private String episodeNumber;


    //@Lob 通常与@Basic同时使用，提高访问速度
    @Lob
    @Basic(fetch = FetchType.LAZY)
    @Column(name=" film_logo", columnDefinition="longblob", nullable=true)
    private byte[] filmLogo;

}
