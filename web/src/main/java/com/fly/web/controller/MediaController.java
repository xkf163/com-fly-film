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

    @GetMapping(value = "/unlink")
    public String mediaUnlink(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/media/unlink");
        return "views/media/unlink";
    }


    @GetMapping(value = "/edit")
    private String edit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/media/edit";
    }

    @GetMapping(value = "/deleted/2")
    public String mediaInProcess(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/media/deleted/2");
        return "views/media/list";
    }


}
