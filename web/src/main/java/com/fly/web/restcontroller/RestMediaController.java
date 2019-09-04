package com.fly.web.restcontroller;


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

    @PostMapping(value = "/duplicate")
    public Map<String, Object> mediaDuplicate(String reqObj) throws Exception {
        return mediaService.findDuplicate(reqObj);
    }


    @PostMapping(value = "/unlink")
    public Map<String, Object> mediaUnlink(String reqObj) throws Exception {
        return mediaService.findUnlink(reqObj);
    }


    @PostMapping(value = "/get")
    private Media getMedia(String id) {
        return mediaService.findOne(Long.parseLong(id));
    }


    @PostMapping(value = "/save")
    private Media saveMedia(Media media) {
        System.out.println("---------------------------------");
        System.out.println(media.getFilm().getId());
        media.setUpdateDate(new Date());
        mediaService.save(media);
        return mediaService.findOne(media.getId());
    }


}
