package com.fly.crawler.entity;

import lombok.Data;

@Data
public class Crawler {

    String url;
    String mutil = "0";
    String homepage = "0";
    String thread;
    String directorEmpty = "0";
    String actorEmpty ="0";
    String batchNumber;
    String filmOnly = "0";
    String personOnly ="0";

    int sleepTime = 10000;

    String loginIn = "0";
    String ratingEmpty = "0";

    String forFilm = "0";

//    String patchBirthday = "0"; //补丁： 出生日期 死亡日期
//    String patchNameOther = "0";//补丁 ： 中文又名 外文又名

}
