package com.fly.web.restcontroller;

import com.fly.entity.Film;
import com.fly.service.FilmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import com.fly.common.base.pojo.ResultBean;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
        }else{
            Film f = filmService.findOne(id);
            film.setFilmLogo(f.getFilmLogo());
            film.setUpdateDate(new Date());
        }



        filmService.save(film);

        return new ResultBean<>(film);
    }


    /**
     * 返回
     * @param
     * @return
     */
    @PostMapping(value = "/upload/{filmId}")
    private ResultBean<Film> uploadLogo(@PathVariable(name = "filmId", required = true) Long filmId,@RequestParam(name="filmLogo",required=false) MultipartFile filmLogo) throws IOException {
        Film film;
        if (filmId!=0){
            film = filmService.findOne(filmId);
            film.setUpdateDate(new Date());
        }else{
            film = new Film();
            film.setCreateDate(new Date());
        }

        if (filmLogo == null){
            film.setFilmLogo(null);
        }else {
            film.setFilmLogo(filmLogo.getBytes());
        }

        filmService.save(film);

        return new ResultBean<>(film);

    }



}
