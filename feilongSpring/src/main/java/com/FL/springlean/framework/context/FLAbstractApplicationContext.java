package com.FL.springlean.framework.context;

/**
 * @author
 */
public abstract class FLAbstractApplicationContext {
    protected void onRefresh() {
    }

    protected abstract void refreshBeanFactory();
}
