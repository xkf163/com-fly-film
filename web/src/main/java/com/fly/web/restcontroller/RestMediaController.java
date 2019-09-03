package com.fly.web.restcontroller;


import com.fly.common.utils.EncryptUtil;
import com.fly.common.utils.StrUtil;
import com.fly.entity.Media;
import com.fly.service.MediaService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/media")
public class RestMediaController {

    @Autowired
    MediaService mediaService;

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
    private void saveMedia(Media media, HttpServletRequest request) {


    }


}
