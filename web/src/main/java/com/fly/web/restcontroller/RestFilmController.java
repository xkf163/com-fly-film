package com.fly.web.restcontroller;

import com.fly.entity.Film;
import com.fly.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

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
    public Map<String, Object> filmAll(String reqObj) throws Exception {
        return filmService.findAll(reqObj);
    }
}
