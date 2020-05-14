package com.jxkj.spring.framework.context.support;

/**
 * 功能描述：Ioc实现的顶层抽象类，实现Ioc容器相关的公共逻辑
 *
 * 简化版，只模拟refresh()方法
 * @author wcx
 * @version 1.0
 */
public abstract class MyAbstractApplicationContext {
    /**
     * 受保护，只提供给子类重写
     * @throws Exception
     */
    protected void refresh() throws Exception{}
}
