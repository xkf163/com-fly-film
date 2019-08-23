package com.fly.web.restcontroller;

import com.fly.entity.Film;
import com.fly.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:02 2019/8/23
 */
@RestController
@RequestMapping(value = "/api/film")
public class RestFilmController {

    @Autowired
    FilmService filmService;

    @PostMapping(value = "/all")
    public Page<Film> filmAll(HttpServletRequest request, @RequestParam(value = "page",defaultValue = "1",required = false) String page,
                              @RequestParam(value = "rows",defaultValue = "10",required = false) String size,
                              @RequestParam(value = "sort",defaultValue = "year",required = false) String sort,
                              @RequestParam(value = "order",defaultValue = "DESC",required = false) String order){
        return filmService.findAll(page,size,sort,order);
    }
}
