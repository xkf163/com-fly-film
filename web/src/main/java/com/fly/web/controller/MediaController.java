package com.fly.web.controller;

import com.fly.common.base.pojo.Result;
import com.fly.service.MediaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * @Author:xukangfeng
 * @Description
 * @Date : Create in 10:26 2019/8/22
 */

@Controller
@RequestMapping(value = "/media")
public class MediaController {


    @Autowired
    MediaService mediaService;

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


    @GetMapping(value = "/allplus")
    public String mediaAllPlus(HttpServletRequest request) {
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/media/all/plus");
        request.setAttribute("dataTableId","media_list_admin");
        request.setAttribute("searchDivUrl","common/search/mediaAdminAll");
//        return "views/media/unlink";
        return "views/pageDefault";
    }

    /**
     * 重复的Media
     * @param request
     * @return
     */
    @GetMapping(value = "/duplicate")
    public String mediaDuplicate(HttpServletRequest request) {
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/media/duplicate");
        request.setAttribute("dataTableId","media_list");
        request.setAttribute("searchDivUrl","common/search/mediaAdminDup");
//        return "views/media/duplicate";
        return "views/pageDefault";
    }

    @GetMapping(value = "/unlink")
    public String mediaUnlink(HttpServletRequest request) {
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/media/unlink");
        request.setAttribute("dataTableId","media_list_admin");
        request.setAttribute("searchDivUrl","common/search/mediaAdminDup");
//        return "views/media/unlink";
        return "views/pageDefault";
    }

    /**
     * 已删除
     * @param request
     * @param deleted
     * @return
     */
    @GetMapping(value = "/deleted/{deleted}")
    public String mediaInProcess(HttpServletRequest request,@PathVariable Integer deleted) {
        String pageSubject=request.getParameter("pageSubject");
        request.setAttribute("pageSubject",pageSubject);
        request.setAttribute("dataUrl", "api/media/deleted/"+deleted);
        request.setAttribute("dataTableId","media_list_admin");
        request.setAttribute("searchDivUrl","common/search/mediaAdminDel");
//        return "views/media/list";
        return "views/pageDefault";
    }

    /**
     * 删除media条目到回收站
     * @param request
     * @param rowId
     * @return
     */
    @PostMapping(value = "/damage/{rowId}")
    public Result mediaDamage(HttpServletRequest request,@PathVariable Long rowId) {
        try {
            Boolean ret = mediaService.damage(rowId);
            if (ret == false){
                return new Result(false);
            }
        } catch (Exception e) {
            return new Result(false);
        }
        return new Result(true);
    }

    /**
     * 销毁条目
     * @param request
     * @param rowId
     */
    @PostMapping(value = "/burned/{rowId}")
    public Result mediaBurned(HttpServletRequest request, @PathVariable Long rowId) {
        try {
            Boolean ret = mediaService.burned(rowId);
            if (ret == false){
                return new Result(false);
            }
        } catch (Exception e) {
            return new Result(false);
        }

        return new Result(true);
    }

    /**
     * media查询主页面，放3张表格，数据串联
     * @param request
     * @return
     */
    @GetMapping(value = "/triple/vertical")
    public String mediaTripleVertical(HttpServletRequest request) {
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
        return "views/pageTripleVertical";
    }


    /**
     * media查询主页面，放3张表格，数据串联
     * @param request
     * @return
     */
    @GetMapping(value = "/triple/horizontal")
    public String mediaTripleHorizontal(HttpServletRequest request) {
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
        return "views/pageTripleHorizontal";
    }






    @GetMapping(value = "/edit")
    private String edit(String id, HttpServletRequest request) {
        request.setAttribute("id", id);
        return "views/media/edit";
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
