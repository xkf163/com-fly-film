package com.fly.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:27 2019/8/22
 */
@Controller
@RequestMapping(value = "/star")
public class StarController {
    @GetMapping(value = "/all")
    public String starAll(HttpServletRequest request) {
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/star/all");
        request.setAttribute("dataTableId","star_list");
        request.setAttribute("searchDivUrl","common/search/star");
        return "views/pageDefault";
    }

    @GetMapping(value = "/list")
    private String starList( HttpServletRequest request) {
        request.setAttribute("dataStarUrl", "api/star/all");
        request.setAttribute("dataMediaUrl", "api/media/list");
        return "views/star/star_list";
    }


    @GetMapping(value = "/edit")
    private String starEdit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/star/star_edit";
    }




}
