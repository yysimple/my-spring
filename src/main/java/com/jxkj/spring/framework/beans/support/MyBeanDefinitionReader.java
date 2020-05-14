package com.jxkj.spring.framework.beans.support;

import com.jxkj.spring.framework.beans.config.MyBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 功能描述：BeanDefinitionReader主要完成对application.properties配置文件的解析工作
 *
 * @author wcx
 * @version 1.0
 */
public class MyBeanDefinitionReader {

    private List<String> registerBeanClasses = new ArrayList<>();

    private Properties config = new Properties();

    /**
     * 配置文件里面的key
     */
    private final String SCAN_PACKAGE = "scanPackage";

    /**
     * 加载配置文件，并转换成流
     *
     * @param configLocations
     */
    public MyBeanDefinitionReader(String... configLocations) {
        InputStream is = this.getClass()
                .getClassLoader()
                .getResourceAsStream(configLocations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));
    }

    private void doScanner(String property) {
        // 转化成文件路劲，实际上是吧 . 换成 /
        URL url = this.getClass().getClassLoader()
                .getResource("/" + property.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        // 遍历指定扫描包下的所有类，并截取其名字放入 registerBeanClasses中进行保存
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(property + "." + file.getName());
            } else {
                if (file.getName().endsWith(".class")) {
                    continue;
                } else {
                    String className = (property + "." + file.getName().replace(".class", ""));
                    registerBeanClasses.add(className);
                }
            }
        }
    }

    /**
     * 把配置文件中扫描到的所有配置信息转换成MyBeanDefinition对象，以便之后的IoC操作
     *
     * @return
     */
    public List<MyBeanDefinition> loadBeanDefinitions() {
        List<MyBeanDefinition> result = new ArrayList<>();
        try {
            for (String className : registerBeanClasses) {
                Class<?> beanClass = Class.forName(className);
                if (beanClass .isInterface()) {
                    continue;
                }
                result.add(doCreateBeanDefinition(toLowerFirstCase(beanClass.getSimpleName()), beanClass.getName()));

            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    /**
     * 把每一个配置信息转换成MyBeanDefinition
     * @param factoryBeanName
     * @param beanClassName
     * @return
     */
    private MyBeanDefinition doCreateBeanDefinition(String factoryBeanName, String beanClassName){
        MyBeanDefinition myBeanDefinition = new MyBeanDefinition();
        myBeanDefinition.setBeanClassName(beanClassName);
        myBeanDefinition.setFactoryBeanName(factoryBeanName);
        return myBeanDefinition;
    }

    /**
     * 将类名的首字母转为小写
     * getName：这个获取的是全限定名 com.jxkj...Wcx
     * getSimpleName：这个获取的就是类名 Wcx
     * @param simpleName
     * @return
     */
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        // 在Java中，对char的算术运算，其实就是对ASCII码做算术运算
        chars[0] += 32;
        return String.valueOf(chars);
    }

    public Properties getConfig() {
        return this.config;
    }
}
