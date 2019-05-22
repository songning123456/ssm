package com.FL.springlean.framework.webmvc;

import lombok.Data;

import java.util.Map;

@Data
public class FLModelAndView {

    private String viewName;
    private Map<String, ?> dataModel;

    public FLModelAndView(String viewName, Map<String, ?> dataModel) {
        this.viewName = viewName;
        this.dataModel = dataModel;
    }
}
