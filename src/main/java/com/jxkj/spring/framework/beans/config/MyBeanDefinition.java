package com.jxkj.spring.framework.beans.config;

import lombok.Data;

/**
 * 功能描述：用来保存Bean、的相关信息
 * 主要用来存储配置文件的信息、相当于保存在内存中的配置
 *
 * @author wcx
 * @version 1.0
 */
@Data
public class MyBeanDefinition {
    /**
     * 原生Bean的全类名
     */
    private String beanClassName;

    /**
     * 标记是否延时加载，默认是false
     */
    private boolean lazyInit = false;

    /**
     * 保存beanName，在Ioc容器中的key
     */
    private String factoryBeanName;
}
