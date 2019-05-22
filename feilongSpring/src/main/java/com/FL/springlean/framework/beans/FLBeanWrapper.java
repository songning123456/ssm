package com.FL.springlean.framework.beans;

/**
 * @author
 * 包装实例化的Bean,以及被代理的bean
 */
public class FLBeanWrapper {
    private Object originalBean;
    private Object wrapperedBean;

    public FLBeanWrapper(Object originalBean) {
        this.originalBean = originalBean;
        this.wrapperedBean = originalBean;
    }

    public Object get_originalBean() {
        return this.originalBean;
    }

    public void set_originalBean(Object originalBean) {
        this.originalBean = originalBean;
    }

    public Object get_wrapperedBean() {
        return this.wrapperedBean;
    }

    public void set_wrapperedBean(Object wrapperedBean) {
        this.wrapperedBean = wrapperedBean;
    }
}
