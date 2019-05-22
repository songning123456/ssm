package com.FL.springlean.framework.webmvc;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author
 */
@Data
public class FLHandlerMapping {
    private Object controller;
    private Method method;
    private Pattern urlPattern;

    public FLHandlerMapping(Object controller, Method method, Pattern urlPattern) {
        this.controller = controller;
        this.method = method;
        this.urlPattern = urlPattern;
    }
}
