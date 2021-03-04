package com.fly.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Created by F on 2017/6/15.
 */
@Controller
public class IndexController {

    @GetMapping(value = "/")
    public String index(){
        return "indexPage";
    }

    @GetMapping(value = "/admin")
    public String admin(){
        return "adminIndex";
    }

    @GetMapping(value = "/dashboardfront")
    public String dashboardFront(){
        return "views/dashboard/front";
    }

    @GetMapping(value = "/dashboardadmin")
    public String dashboardAdmin(){
        return "views/dashboard/admin";
    }


}
