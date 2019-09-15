package com.fly.service;


import com.fly.common.base.pojo.ResultBean;
import com.fly.entity.Media;
import com.fly.pojo.Relevance;

import java.util.List;

public interface RelevanceService {

    ResultBean<String> relevantFilmForMedia(Relevance relevance);

    String runningRecord();

}
