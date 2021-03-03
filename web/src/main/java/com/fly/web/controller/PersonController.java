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
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/person/all");
        request.setAttribute("dataTableId","person_list");
        request.setAttribute("searchDivUrl","common/search/person");
        return "views/pageDefault";
    }


    @GetMapping(value = "/list")
    private String personFilmList( HttpServletRequest request) {
        request.setAttribute("dataPersonUrl", "api/person/all");
        request.setAttribute("dataFilmUrl", "api/film/list");
        return "views/person/person_list";
    }


    @GetMapping(value = "/edit")
    private String personEdit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/person/person_edit";
    }



    /**
     * 豆瓣影库/按人物
     * @param request
     * @return
     */
    @GetMapping(value = "/query/all")
    public String personQueryHorizontal(HttpServletRequest request) {
        String pageSubject = request.getParameter("pageSubject");

        request.setAttribute("pageSubject", pageSubject);
        request.setAttribute("dataMainTable", "api/person/all");
        request.setAttribute("dataMainTableId", "mainPerson");
        request.setAttribute("dataSearchDivMainHtml", "common/search/person");

        request.setAttribute("dataSubTopTable", "api/film/all");
        request.setAttribute("dataSubTopTableId", "subFilm");

        request.setAttribute("dataSubBottomTable", "api/person/all");
        request.setAttribute("dataSubBottomTableId", "subPerson");

        return "views/person/personTripleHorizontal";
    }

}
