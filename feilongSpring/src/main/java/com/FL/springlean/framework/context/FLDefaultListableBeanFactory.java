package com.FL.springlean.framework.context;

import com.FL.springlean.framework.beans.FLBeanDefinition;
import com.FL.springlean.framework.beans.FLBeanWrapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FLDefaultListableBeanFactory extends FLAbstractApplicationContext {

    //用来存储BeanDefinition的定义
    protected Map<String,FLBeanDefinition> beanDefinitionMap= new ConcurrentHashMap<>();
    //用来存储实例化的单例Bean对象
    protected Map<String,FLBeanWrapper>  singleBeanInstanceMap= new ConcurrentHashMap<>();
//    protected Map<String,FLBeanWrapper>  singleBeanInstanceMap= new ConcurrentHashMap<>();

    protected List<String> beanDefinitionNames = new ArrayList<>();

    @Override
    protected void refreshBeanFactory() {

    }
}
