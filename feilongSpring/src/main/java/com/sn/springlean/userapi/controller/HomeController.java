package com.sn.springlean.userapi.controller;

import com.sn.springlean.framework.annotation.SnAutowired;
import com.sn.springlean.framework.annotation.SnController;
import com.sn.springlean.framework.annotation.SnRequestMapping;
import com.sn.springlean.framework.annotation.SnRequestParam;
import com.sn.springlean.framework.webmvc.SnModelAndView;
import com.sn.springlean.userapi.services.ModifyService;
import com.sn.springlean.userapi.services.QueryService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@SnController
@SnRequestMapping("/web")
public class HomeController {

    @SnAutowired
    QueryService queryService;

    @SnAutowired
    ModifyService modifyService;


    @SnRequestMapping("/query")
    public SnModelAndView query(HttpServletRequest request, HttpServletResponse response,
                                @SnRequestParam("name") String name) {
        String result = queryService.query(name);
        Map mapBody = new HashMap(8);
        mapBody.put("data", "this is a data set from controller");
        mapBody.put("token", "beautifull token set here");
        SnModelAndView mv = new SnModelAndView("first.html", mapBody);
        return mv;
//        return out(response,result);
    }

    @SnRequestMapping("/insert")
    public SnModelAndView insert(HttpServletRequest request, HttpServletResponse response,
                                 @SnRequestParam("name") String name) {
        String result = queryService.query(name);
        Map mapBody = new HashMap(16);
        mapBody.put("data", "insert");
        mapBody.put("token", "insert");
        SnModelAndView mv = new SnModelAndView("first.html", mapBody);
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
