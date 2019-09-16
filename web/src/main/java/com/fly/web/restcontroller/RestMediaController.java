package com.fly.web.restcontroller;


import com.fly.common.base.pojo.ResultBean;
import com.fly.common.utils.EncryptUtil;
import com.fly.common.utils.StrUtil;
import com.fly.dao.FilmRepository;
import com.fly.entity.Film;
import com.fly.entity.Media;
import com.fly.service.FilmService;
import com.fly.service.MediaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/media")
public class RestMediaController {

    @Autowired
    MediaService mediaService;

    @Autowired
    FilmService filmService;


    @Autowired
    FilmRepository filmRepository;

    @PostMapping(value = "/all")
    public Map<String, Object> mediaAll(String reqObj) throws Exception {
        return mediaService.findAll(reqObj);
    }

    @PostMapping(value = "/list")
    public Map<String, Object> mediaAllOfStar(String reqObj) throws Exception {
        return mediaService.findAllOfStar(reqObj);
    }

    @PostMapping(value = "/series")
    public Map<String, Object> mediaAllOfSeries(String reqObj) throws Exception {
        return mediaService.findAllOfSeries(reqObj);
    }

    @PostMapping(value = "/series/unselect")
    public Map<String, Object> mediaAllOfSeriesUnselect(String reqObj) throws Exception {
        return mediaService.findAllOfSeriesUnselect(reqObj);
    }

    @PostMapping(value = "/duplicate")
    public Map<String, Object> mediaDuplicate(String reqObj) throws Exception {
        return mediaService.findDuplicate(reqObj);
    }


    @PostMapping(value = "/unlink")
    public Map<String, Object> mediaUnlink(String reqObj) throws Exception {
        return mediaService.findUnlink(reqObj);
    }


    @PostMapping(value = "/deleted/{deleted}")
    public Map<String, Object> mediaInTrash(String reqObj,@PathVariable Integer deleted) throws Exception {
        return mediaService.findByDeleted(reqObj,deleted);
    }





    @PostMapping(value = "/get")
    private Media getMedia(String id) {
        return mediaService.findOne(Long.parseLong(id));
    }


    /**
     *
     * @param media
     * @param filmId 关联的Film对象
     * @return
     */
    @PostMapping(value = "/save")
    private ResultBean<Media> saveMedia(Media media, @RequestParam(name = "filmId", required = false ,defaultValue = "0" ) Long filmId) {

        Film film = null;
        if (filmId > 0){
            film = filmService.findOne(filmId);
        }
        media.setFilm(film);
        media.setUpdateDate(new Date());
        mediaService.save(media);

        return new ResultBean<>(media);
    }



    /**
     *
     * @param media
     * @param filmId 关联的Film对象
     * @return
     */
    @PostMapping(value = "/delete")
    private ResultBean<Media> deleteMedia(Media media, @RequestParam(name = "filmId", required = false ,defaultValue = "0" ) Long filmId) {
        Film film = null;
        if (filmId > 0){
            film = filmService.findOne(filmId);
        }
        media.setFilm(film);
        media.setUpdateDate(new Date());

        //2表示更改待更改完成状态（需要确认后再变回0状态），1表示删除状态，0表示正常状态，
        media.setDeleted(1);
        mediaService.save(media);

        return new ResultBean<>(media);
    }


    @PostMapping(value = "/destroy")
    public ResultBean<Media> mediaDestroy(Media media, @RequestParam(name = "filmId", required = false ,defaultValue = "0" ) Long filmId) throws Exception {
        mediaService.delete(media);
        return new ResultBean<>(media);
    }


    /**
     *
     * @param media
     * @param filmId 关联的Film对象
     * @return
     */
    @PostMapping(value = "/saveToProcess")
    private ResultBean<Media> saveMediaToProcess(Media media, @RequestParam(name = "filmId", required = false ,defaultValue = "0" ) Long filmId) {
        Film film = null;
        if (filmId > 0){
            film = filmService.findOne(filmId);
        }
        media.setFilm(film);
        media.setUpdateDate(new Date());

        //2表示更改待更改完成状态（需要确认后再变回0状态），1表示删除状态，0表示正常状态，
        media.setDeleted(2);
        mediaService.save(media);

        return new ResultBean<>(media);
    }



    /**
     *
     * @param media
     * @param filmId 关联的Film对象
     * @return
     */
    @PostMapping(value = "/saveToFinish")
    private ResultBean<Media> saveMediaToFinish(Media media, @RequestParam(name = "filmId", required = false ,defaultValue = "0" ) Long filmId) {
        Film film = null;
        if (filmId > 0){
            film = filmService.findOne(filmId);
        }
        media.setFilm(film);
        media.setUpdateDate(new Date());

        //2表示更改待更改完成状态（需要确认后再变回0状态），1表示删除状态，0表示正常状态，
        media.setDeleted(0);
        mediaService.save(media);
        return new ResultBean<>(media);
    }






}
