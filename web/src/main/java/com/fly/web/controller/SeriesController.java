package com.fly.web.controller;

import com.fly.common.base.pojo.Result;
import com.fly.entity.Series;
import com.fly.service.SeriesService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.fly.common.utils.StrUtil;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:26 2019/8/22
 */

@Controller
@RequestMapping(value = "/series")
public class SeriesController {


    @Autowired
    SeriesService seriesService;

    @GetMapping(value = "/all")
    public String seriesAll(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/series/all");
        return "views/series/list";
    }


    /**
     * 系列编辑
     *
     * @return
     */
    @GetMapping(value = "/edit")
    private String edit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/series/edit";
    }



    @RequestMapping(method = RequestMethod.POST, value = "/save")
    @ResponseBody
    private Result saveUser(Series series, HttpServletRequest request) {
        if ( StrUtil.isEmpty( String.valueOf(series.getId()) ) ) {

            seriesService.save(series);

        } else {
            Series old=this.getSeries(series.getId());
//            BeanUtils.copyProperties(user,oldUser,"password");
//            if(!oldUser.getLoginName().equals(user.getLoginName())){
//                oldUser.setPassword(EncryptUtil.getPassword(initPassword,user.getLoginName()));
//            }
//            oldUser.setUpdateDateTime(new Date());
//            userService.update(oldUser);
        }
        return new Result(true);
    }



    @RequestMapping(method = RequestMethod.POST, value = "/get")
    @ResponseBody
    private Series getSeries(Long id) {
       // return seriesService.get(Series.class, id);
        return null;
    }

}
