package com.fly.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 搜索div的html代码
 */

@Controller
@RequestMapping(value = "/search")
public class SearchDivController {

    @GetMapping(value = "/media")
    public String scannerNew(){
        return "views/search/media";
    }
}
