package com.sn.springlean.framework.webmvc;

import lombok.Data;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

/**
 * @author
 */
@Data
public class SnHandlerMapping {
    private Object controller;
    private Method method;
    private Pattern urlPattern;

    public SnHandlerMapping(Object controller, Method method, Pattern urlPattern) {
        this.controller = controller;
        this.method = method;
        this.urlPattern = urlPattern;
    }
}
