package com.jxkj.spring.framework.core;

/**
 * 功能描述：Bean工厂作为最顶层的一个接口类，定义了Ioc容器的基本功能规范
 *
 * 单例工厂的顶层设计
 * @author wcx
 * @version 1.0
 */
public interface MyBeanFactory {
    /**
     * 通过对象名从Ioc容器里面获取一个Bean实例
     * @param beanName
     * @return
     * @throws Exception
     */
    Object getBean(String beanName) throws Exception;

    /**
     * 通过对象的类类型从Ioc容器里面获取一个Bean实例
     * @param beanClass
     * @return
     * @throws Exception
     */
    Object getBean(Class<?> beanClass) throws Exception;
}
