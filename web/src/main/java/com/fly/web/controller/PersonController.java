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
@RequestMapping(value = "/person")
public class PersonController {
    @GetMapping(value = "/all")
    public String personAll(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/person/all");
        return "views/person/list";
    }


    @GetMapping(value = "/edit")
    private String personEdit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/person/person_edit";
    }

}
