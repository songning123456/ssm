package com.FL.springlean.userapi.controller;

import com.FL.springlean.framework.annotation.FLAutowired;
import com.FL.springlean.framework.annotation.FLController;
import com.FL.springlean.framework.annotation.FLRequestMapping;
import com.FL.springlean.framework.annotation.FLRequestParam;
import com.FL.springlean.framework.webmvc.FLModelAndView;
import com.FL.springlean.userapi.services.IModifyService;
import com.FL.springlean.userapi.services.IQueryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@FLController
@FLRequestMapping("/web")
public class HomeController {

    @FLAutowired
    IQueryService queryService;

    @FLAutowired
    IModifyService modifyService;


    @FLRequestMapping("/query")
    public FLModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                @FLRequestParam("name") String name) {
        String result = queryService.query(name);
        Map mapBody = new HashMap(8);
        mapBody.put("data", "this is a data set from controller");
        mapBody.put("token", "beautifull token set here");
        FLModelAndView mv = new FLModelAndView("first.html", mapBody);
        return mv;
//        return out(response,result);
    }

    @FLRequestMapping("/insert")
    public FLModelAndView insert(HttpServletRequest request, HttpServletResponse response,
                                 @FLRequestParam("name") String name) {
        String result = queryService.query(name);
        Map mapBody = new HashMap(16);
        mapBody.put("data", "insert");
        mapBody.put("token", "insert");
        FLModelAndView mv = new FLModelAndView("first.html", mapBody);
        return mv;
    }


    private String out(HttpServletResponse resp, String str) {
        try {
            resp.getWriter().write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
