package com.fly.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:26 2019/8/22
 */

@Controller
@RequestMapping(value = "/media")
public class MediaController {

    @GetMapping(value = "/all")
    public String mediaAll(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/media/all");
        return "views/media/list";
    }


    @GetMapping(value = "/duplicate")
    public String mediaDuplicate(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/media/duplicate");
        return "views/media/duplicate";
    }
}
