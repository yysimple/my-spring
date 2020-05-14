package com.jxkj.spring.framework.context.support;

import com.jxkj.spring.framework.beans.config.MyBeanDefinition;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述：定义顶层的Ioc缓存，也就是一个Map
 *
 * @author wcx
 * @version 1.0
 */
public class MyDefaultListableBeanFactory extends MyAbstractApplicationContext{
    /**
     * 存储注册信息BeanDefinition的map
     */
    protected final Map<String, MyBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<>();
}
