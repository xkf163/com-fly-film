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
        request.setAttribute("dataUrl", "api/star/all");
        return "views/star/list";
    }


    @GetMapping(value = "/edit")
    private String starEdit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/star/star_edit";
    }

}
