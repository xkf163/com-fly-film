package com.fly.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by F on 2017/6/15.
 */
@RestController
public class IndexController {

    @GetMapping(value = "/")
    public String index(){
        return "helloworld";
    }

}
