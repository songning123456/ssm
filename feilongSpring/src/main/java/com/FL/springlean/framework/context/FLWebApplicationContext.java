package com.FL.springlean.framework.context;

import com.FL.springlean.framework.annotation.FLAutowired;
import com.FL.springlean.framework.annotation.FLController;
import com.FL.springlean.framework.annotation.FLService;
import com.FL.springlean.framework.beans.FLBeanDefinition;
import com.FL.springlean.framework.beans.FLBeanWrapper;
import com.FL.springlean.framework.context.support.FLBeanDefinitionReader;
import com.FL.springlean.framework.core.FLBeanFactory;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class FLWebApplicationContext extends FLDefaultListableBeanFactory implements FLBeanFactory {
    private String[] configLocations;
    private FLBeanDefinitionReader reader;

    //用来保证注册式单例的容器
    //key 为clasname
    private Map<String, Object> beanCacheMap = new HashMap<String, Object>();

    public FLWebApplicationContext(String... configLocations) {
        this.configLocations = configLocations;

        refresh();
    }

    private void refresh() {

        //定位
        if (reader == null) {
            reader = new FLBeanDefinitionReader(configLocations[0]);
        }
        //加载

        List<String> beanDefinitionClassNames = reader.getRegistedBeanDefinitionsClassName();

        //注册
        doRegister(beanDefinitionClassNames);

        //依赖注入
        doAutowrited();

    }


    public String[] getBeanDefinitions() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    private void doRegister(List<String> beanDefinitions) {
        try {

            for (String BeanClassName : beanDefinitions) {

                Class<?> clazz = Class.forName(BeanClassName);
                if (clazz.isInterface()) {
                    continue;
                }

                FLBeanDefinition beanDefinition = reader.doRegister(BeanClassName);
                if (beanDefinition == null) {
                    continue;
                }
                this.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
                this.beanDefinitionNames.add(beanDefinition.getFactoryBeanName());


                for (Class<?> i : clazz.getInterfaces()) {
                    this.beanDefinitionMap.put(i.getName(), beanDefinition);
                    this.beanDefinitionNames.add(i.getName());
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doAutowrited() {

        for (Map.Entry<String, FLBeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            //getBean 是DI的开始
            getBean(entry.getKey());
        }

        //对属性进行注入

        for (Map.Entry<String, FLBeanWrapper> entry : this.singleBeanInstanceMap.entrySet()) {
            //getBean 是DI的开始
            populateBean(entry.getKey(), entry.getValue());
        }
    }

    //遍历Bean下所有属性, 进行注入
    private void populateBean(String strBeanName, FLBeanWrapper instance) {
        Object originalInstance = instance.get_originalBean();
        Class<?> clazz = originalInstance.getClass();

        if (!(clazz.isAnnotationPresent(FLController.class) || clazz.isAnnotationPresent(FLService.class))) {
            return;
        }

        Field[] fields = clazz.getDeclaredFields();
        for (Field fd : fields) {
            if (!fd.isAnnotationPresent(FLAutowired.class)) {
                continue;
            }
            FLAutowired autowired = fd.getAnnotation(FLAutowired.class);
            String strAutowireName = autowired.value().trim();
            if (strAutowireName.equals("")) {
                strAutowireName = fd.getType().getName();
            }
            fd.setAccessible(true);

            try {
                fd.set(originalInstance, this.singleBeanInstanceMap.get(strAutowireName).get_wrapperedBean());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }


    }

    @Override
    public Object getBean(String beanName) {

        if (this.singleBeanInstanceMap.containsKey(beanName)) {
            return this.singleBeanInstanceMap.get(beanName);
//            return this.singleBeanInstanceMap.get(beanName).get_wrapperedBean();
        }

        FLBeanDefinition beanDefinition = this.beanDefinitionMap.get(beanName);
        if (beanDefinition == null) return null;

        Object beanInstance = initBean(beanDefinition);

        FLBeanWrapper beanWrapper = new FLBeanWrapper(beanInstance);

        this.singleBeanInstanceMap.put(beanName, beanWrapper);

        return beanWrapper;

    }

    private Object initBean(FLBeanDefinition beanDefinition) {
        Object instance = null;
        String beanClassName = beanDefinition.getBeanClassName();
        try {

            if (beanCacheMap.containsKey(beanClassName)) {
                return beanCacheMap.get(beanClassName);
            }


            Class<?> clazz = (Class<?>) beanDefinition.getBeanClass();
            instance = clazz.newInstance();
            this.beanCacheMap.put(beanClassName, instance);
            return instance;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }


    public Properties getConfig(){
        return this.reader.getProperties();
    }
}
