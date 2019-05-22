package com.sn.springlean.framework.context;

import com.sn.springlean.framework.beans.SnBeanDefinition;
import com.sn.springlean.framework.beans.SnBeanWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author sn
 * 用来模拟DefaultListableBeanFactory, 这就是IOC容器
 */
public class SnDefaultListableBeanFactory extends SnAbstractApplicationContext {

    /**
     * 用来存储BeanDefinition的定义
     */
    Map<String, SnBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
    /**
     * 用来存储实例化的单例Bean对象
     */
    Map<String, SnBeanWrapper> singleBeanInstanceMap = new ConcurrentHashMap<>();

    List<String> beanDefinitionNames = new ArrayList<>();

    @Override
    protected void refreshBeanFactory() {

    }
}
