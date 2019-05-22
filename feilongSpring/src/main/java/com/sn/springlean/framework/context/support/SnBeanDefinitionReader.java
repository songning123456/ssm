package com.sn.springlean.framework.context.support;

import com.sn.springlean.framework.beans.SnBeanDefinition;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author sn
 * 用来读取xml文件及向ioc中注册bean
 * 对配置文件进行解析, 涉及到对Bean的定位,加载,注册,注入等.
 */
public class SnBeanDefinitionReader {
    private Properties properties = new Properties();

    public Properties getProperties() {
        return properties;
    }

    private List<String> registeredBeanDefinitionsClassName = new ArrayList<>();


    public SnBeanDefinitionReader(String... locations) {

        //定位
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));

        //加载
        try {
            properties.load(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        //注册 (获取要扫描的路径 strPackage = com.sn.springlean.userapi)
        String strPackage = properties.get("scanPackage").toString();

        // List<String> registeredBeanDefinitionsClassName 记录下配置文件中Bean包下所有的类(全路径名称)
        doScannerBeanClassNames(strPackage);

    }

    private static String lowerFirstCase(String str) {
        char[] chars = str.toCharArray();
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public SnBeanDefinition doRegister(String beanClassName) {

        if (this.registeredBeanDefinitionsClassName.contains(beanClassName)) {
            SnBeanDefinition beanDefinition = new SnBeanDefinition(beanClassName, lowerFirstCase(beanClassName.substring(beanClassName.lastIndexOf(".") + 1)));
            return beanDefinition;
        }
        return null;
    }

    /**
     * 扫描指定包下所有的类
     *
     * @param packageName
     */
    private void doScannerBeanClassNames(String packageName) {

        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replaceAll("\\.", "/"));

        assert url != null;
        File classDir = new File(url.getFile());
        for (File fileT : Objects.requireNonNull(classDir.listFiles())) {
            if (fileT.isDirectory()) {
                doScannerBeanClassNames(packageName + "." + fileT.getName());
            } else {
                registeredBeanDefinitionsClassName.add(packageName + "." + fileT.getName().replace(".class", ""));
            }
        }
    }

    public List<String> getRegisteredBeanDefinitionsClassName() {
        return registeredBeanDefinitionsClassName;
    }

}
