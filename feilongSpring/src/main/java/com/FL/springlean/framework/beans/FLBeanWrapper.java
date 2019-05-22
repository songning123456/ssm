package com.FL.springlean.framework.beans;

import lombok.Data;

/**
 * @author 包装实例化的Bean, 以及被代理的bean
 */
@Data
public class FLBeanWrapper {
    private Object originalBean;
    private Object wrappedBean;

    public FLBeanWrapper(Object originalBean) {
        this.originalBean = originalBean;
        this.wrappedBean = originalBean;
    }
}
