package com.jxkj.spring.framework.beans.support;

/**
 * 功能描述：通知事件类，为对象初始化事件设置的一种回调机制
 *
 * @author wcx
 * @version 1.0
 */
public class MyBeanPostProcessor {

    /**
     *  为在Bean的初始化之前提供的回调入口
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }

    /**
     *  为在Bean的初始化之后提供的回调入口
     * @param bean
     * @param beanName
     * @return
     * @throws Exception
     */
    public Object postProcessAfterInitialization(Object bean, String beanName) throws Exception{
        return bean;
    }


}
