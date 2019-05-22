package com.sn.springlean.framework.context;

/**
 * @author sn
 */
public abstract class SnAbstractApplicationContext {
    protected void onRefresh() {
    }

    protected abstract void refreshBeanFactory();
}
