package com.FL.springlean.framework.context.support;

import com.FL.springlean.framework.beans.FLBeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author 对配置文件进行解析, 涉及到对Bean的定位,加载,注册,注入等.
 */
public class FLBeanDefinitionReader {
    private final String SCAN_Package = "";
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    private List<String> registeredBeanDefinitionsClassName = new ArrayList<>();


    public FLBeanDefinitionReader(String... locations) {

        //定位
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));

        //加载
        try {
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //注册
        String strPackage = properties.get("scanPackage").toString();

        //记录下配置文件中Bean包下所有的类
        doScannerBeanClassnames(strPackage);

    }

    private static String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public FLBeanDefinition doRegister(String beanClassName) {

        if (this.registeredBeanDefinitionsClassName.contains(beanClassName)) {
            FLBeanDefinition beanDefinition = new FLBeanDefinition(beanClassName, lowerFirstCase(beanClassName.substring(beanClassName.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

    /**
     * 扫描指定包下所有的类
     *
     * @param packageName
     */
    private void doScannerBeanClassnames(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));

        File classDir = new File(url.getFile());
        for (File fileT : classDir.listFiles()) {
            if (fileT.isDirectory()) {
                doScannerBeanClassnames(packageName + "." + fileT.getName());
            } else {
                registeredBeanDefinitionsClassName.add(packageName + "." + fileT.getName().replace(".class", ""));
            }
        }
    }

    public List<String> getRegistedBeanDefinitionsClassName() {
        return registeredBeanDefinitionsClassName;
    }

}
