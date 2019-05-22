package com.FL.springlean.framework.context;

import com.FL.springlean.framework.beans.FLBeanDefinition;
import com.FL.springlean.framework.beans.FLBeanWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author
 * 用来模拟DefaultListableBeanFactory, 这就是IOC容器
 */
public class FLDefaultListableBeanFactory extends FLAbstractApplicationContext {

    /**
     * 用来存储BeanDefinition的定义
     */
    Map<String, FLBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    /**
     * 用来存储实例化的单例Bean对象
     */
    Map<String, FLBeanWrapper> singleBeanInstanceMap = new ConcurrentHashMap<>();

    List<String> beanDefinitionNames = new ArrayList<>();

    @Override
    protected void refreshBeanFactory() {

    }
}
