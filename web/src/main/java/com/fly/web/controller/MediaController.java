package com.fly.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:26 2019/8/22
 */

@Controller
@RequestMapping(value = "/media")
public class MediaController {

    @GetMapping(value = "/all")
    public String mediaAll(HttpServletRequest request) {
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/media/all");
        request.setAttribute("dataTableId","media_list");
        request.setAttribute("searchDivUrl","common/search/media");
        return "views/pageDefault";
        //return "views/media/list";
    }

    /**
     * media查询主页面，放3张表格，数据串联
     * @param request
     * @return
     */
    @GetMapping(value = "/triple")
    public String mediaTriple(HttpServletRequest request) {
        String pageSubject = request.getParameter("pageSubject");
        request.setAttribute("pageSubject", pageSubject);
        request.setAttribute("dataMainTable", "api/media/all");
        request.setAttribute("dataMainTableId", "mainMedia");
        request.setAttribute("dataSearchDivMainHtml", "common/search/media");

        request.setAttribute("dataSubTopTable", "api/star/all");
        request.setAttribute("dataSubTopTableId", "subStar");
        //request.setAttribute("dataSearchDivSubTopHtml", "common/search/star");

        request.setAttribute("dataSubBottomTable", "api/media/all");
        request.setAttribute("dataSubBottomTableId", "subMedia");

//       return "views/media/triple";
        return "views/pageTriple";
    }

    @GetMapping(value = "/duplicate")
    public String mediaDuplicate(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/media/duplicate");
        return "views/media/duplicate";
    }

    @GetMapping(value = "/unlink")
    public String mediaUnlink(HttpServletRequest request) {
        request.setAttribute("dataUrl", "api/media/unlink");
        return "views/media/unlink";
    }


    @GetMapping(value = "/edit")
    private String edit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/media/edit";
    }

    @GetMapping(value = "/deleted/{deleted}")
    public String mediaInProcess(HttpServletRequest request,@PathVariable Integer deleted) {
        request.setAttribute("dataUrl", "api/media/deleted/"+deleted);
        return "views/media/list";
    }

    /**
     * Media选择
     *
     * @return
     */
    @GetMapping( value = "/select")
    private String select(String seriesId, HttpServletRequest request) {

        request.setAttribute("seriesId", seriesId);
        return "views/media/seriesmedia_select";
    }


}
