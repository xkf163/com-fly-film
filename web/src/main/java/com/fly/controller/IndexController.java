package com.fly.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by F on 2017/6/15.
 */
@Controller
public class IndexController {

    @GetMapping(value = "/")
    public String index(){
        return "framework";
    }

}
