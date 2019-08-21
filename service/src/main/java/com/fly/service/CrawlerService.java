package com.fly.service;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 14:24 2017/11/1
 */
public interface CrawlerService {
    Object[] running(String mutil, String url, String thread, String homepage, String batchNumber, String keySearch, String onePage, String directorEmpty, String actorEmpty);
}

