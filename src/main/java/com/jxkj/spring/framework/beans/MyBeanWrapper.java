package com.jxkj.spring.framework.beans;

/**
 * 功能描述：主要用封装创建后的对象实例，代理对象或者原生对象，都由Wrapper保存
 *
 * @author wcx
 * @version 1.0
 */
public class MyBeanWrapper {

    private Object wrappedInstance;

    private Class<?> wrappedClass;

    public MyBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance(){
        return this.wrappedInstance;
    }

    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
