package com.sn.springlean.framework.beans;

import lombok.Data;

/**
 * @author sn
 * 包装实例化的Bean, 以及被代理的bean
 */
@Data
public class SnBeanWrapper {
    private Object originalBean;
    private Object wrappedBean;

    public SnBeanWrapper(Object originalBean) {
        this.originalBean = originalBean;
        this.wrappedBean = originalBean;
    }
}
