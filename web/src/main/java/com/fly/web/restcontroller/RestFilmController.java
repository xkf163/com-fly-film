package com.fly.web.restcontroller;

import com.fly.entity.Film;
import com.fly.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.fly.common.base.pojo.ResultBean;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.fly.entity.QMedia.media;

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


    @PostMapping(value = "/list")
    public Map<String, Object> filmAllOfPerson(String reqObj) throws Exception {
        return filmService.filmAllOfPerson(reqObj);
    }

    @PostMapping(value = "/get")
    private Film getFilm(String id) {
        return filmService.findOne(Long.parseLong(id));
    }


    /**
     *
     * @param film
     * @return
     */
    @PostMapping(value = "/save")
    private ResultBean<Film> saveFilm(Film film) {

        Long id = film.getId();
        if (id == null ){
            film.setCreateDate(new Date());
        }

        film.setUpdateDate(new Date());
        filmService.save(film);

        return new ResultBean<>(film);
    }

}
