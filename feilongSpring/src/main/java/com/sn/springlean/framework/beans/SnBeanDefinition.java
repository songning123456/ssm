package com.sn.springlean.framework.beans;

import lombok.Data;

/**
 * @author sn
 * 保存XML中Bean被解析后的基本信息
 */
@Data
public class SnBeanDefinition {

    private String beanClassName;
    private String factoryBeanName;
    private Object beanClass;

    public SnBeanDefinition(String beanName, String factoryBeanName) {
        this.beanClassName = beanName;
        this.factoryBeanName = factoryBeanName;

        try {
            Class<?> clazz = Class.forName(beanName);
            this.beanClass = clazz;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
