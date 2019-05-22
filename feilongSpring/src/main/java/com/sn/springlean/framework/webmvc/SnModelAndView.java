package com.sn.springlean.framework.webmvc;

import lombok.Data;

import java.util.Map;

@Data
public class SnModelAndView {

    private String viewName;
    private Map<String, ?> dataModel;

    public SnModelAndView(String viewName, Map<String, ?> dataModel) {
        this.viewName = viewName;
        this.dataModel = dataModel;
    }
}
