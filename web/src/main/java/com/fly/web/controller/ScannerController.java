package com.fly.web.controller;

import com.fly.scanner.entity.Scanner;
import com.fly.scanner.service.ScannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.File;

@Controller
@RequestMapping(value = "/scanner")
public class ScannerController {

    @Autowired
    ScannerService scannerService;


    @GetMapping(value = "/new")
    public String scannerNew(){
        return "views/scanner/new";
    }

    @PostMapping(value = "/start")
    @ResponseBody
    public void scannerStart(Scanner scanner){

       // String pcName = scanner.getPcName();
       // String diskName = scanner.getDiskName();
        String mediaFolder = scanner.getMediaFolder();
        File file = new File(mediaFolder);
        if (file.exists()) {
            scannerService.scanMedia2DB(file,scanner);
        }


    }



}
