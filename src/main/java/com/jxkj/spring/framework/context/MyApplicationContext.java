package com.jxkj.spring.framework.context;

import com.jxkj.spring.framework.annotation.MyAutowired;
import com.jxkj.spring.framework.annotation.MyController;
import com.jxkj.spring.framework.annotation.MyService;
import com.jxkj.spring.framework.beans.MyBeanWrapper;
import com.jxkj.spring.framework.beans.config.MyBeanDefinition;
import com.jxkj.spring.framework.beans.support.MyBeanDefinitionReader;
import com.jxkj.spring.framework.beans.support.MyBeanPostProcessor;
import com.jxkj.spring.framework.context.support.MyDefaultListableBeanFactory;
import com.jxkj.spring.framework.core.MyBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 功能描述：直接接触用户的入口，主要实现refresh() 和 BeanFactory里面的 getBean()方法
 * 完成IoC、DI、AOP的衔接
 *
 * @author wcx
 * @version 1.0
 */
public class MyApplicationContext extends MyDefaultListableBeanFactory implements MyBeanFactory {

    /**
     * 配置文件的路径
     */
    private String[] configLocations;

    /**
     * 解析配置文件的类
     */
    private MyBeanDefinitionReader reader;

    /**
     * 单例Ioc容器的缓存
     */
    private Map<String, Object> factoryBeanObjectCache = new ConcurrentHashMap<>();

    /**
     * 通用的Ioc容器的缓存
     */
    private Map<String, MyBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<>();

    /**
     * 通过构造方法传入配置文件路劲
     *
     * @param configLocations
     */
    public MyApplicationContext(String... configLocations) {
        this.configLocations = configLocations;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void refresh() throws Exception {
        //1. 传入配置文件
        reader = new MyBeanDefinitionReader(this.configLocations);

        //2. 加载配置文件并解析，扫描相关的类，把它们封装成BeanDefinition
        List<MyBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3. 注册，将配置信息放到容器里面（伪IoC容器）
        doRegisterBeanDefinition(beanDefinitions);

        //4. 把不是延迟加载的类提前初始化
        doAutowired();

    }

    /**
     * 处理非延时加载的情况
     */
    private void doAutowired() {
        for (Map.Entry<String, MyBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            // 获取bean的名称
            String beanName = beanDefinitionEntry.getKey();
            // 如果不是懒加载，直接让其获取到对应的bean
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 注册bean到缓存容器中
     *
     * @param beanDefinitions
     * @throws Exception
     */
    private void doRegisterBeanDefinition(List<MyBeanDefinition> beanDefinitions) throws Exception {
        for (MyBeanDefinition beanDefinition : beanDefinitions) {
            if (super.beanDefinitionMap.containsKey(beanDefinition.getFactoryBeanName())) {
                throw new Exception("The " + beanDefinition.getFactoryBeanName() + "is exist!!");
            }
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }

    /**
     * 开始读取beanDefinition里面bean的信息，然后通过反射机制创建一个实例并返回
     * spring里面，不会把最原始的bean返回，会有beanWrapper进行一次包装，这是装饰器模式
     */
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    /**
     * 返回缓存中的bean数量
     *
     * @return
     */
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    /**
     * 返回配置信息
     *
     * @return
     */
    public Properties getConfig() {
        return this.reader.getConfig();
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        // 从缓存中取出指定的 BeanDefinition
        MyBeanDefinition myBeanDefinition = super.beanDefinitionMap.get(beanName);
        try {
            // 生成通知事件
            MyBeanPostProcessor beanPostProcessor = new MyBeanPostProcessor();
            // BeanDefinition 转成指定的 bean
            Object instance = instantiateBean(myBeanDefinition);
            if (null == instance) {
                // 这里可以抛出异常或者友好提示
                return null;
            }

            // 在实例化之前调用一次
            beanPostProcessor.postProcessBeforeInitialization(instance, beanName);

            // 将实例化的bean进行包装再返回
            MyBeanWrapper myBeanWrapper = new MyBeanWrapper(instance);

            // 将其保存到包装缓存中
            this.factoryBeanInstanceCache.put(beanName, myBeanWrapper);

            // 在实例之后调用一次
            beanPostProcessor.postProcessAfterInitialization(instance, beanName);

            // 注入DI
            populateBean(beanName, instance);

            return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void populateBean(String beanName, Object instance) {
        Class clazz = instance.getClass();
        // 判断该实例上面的注解类型
        if (!(clazz.isAnnotationPresent(MyController.class)) || (clazz.isAnnotationPresent(MyService.class))) {
            return;
        }
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(MyAutowired.class)) {
                continue;
            }
            MyAutowired autowired = field.getAnnotation(MyAutowired.class);
            String autowiredBeanName = autowired.value().trim();

            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }
            field.setAccessible(true);
            try {
                // 通过注解，将bean实例化
                field.set(instance, this.factoryBeanInstanceCache.get(beanName).getWrappedInstance());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 传一个 BeanDefinition 返回一个bean
     * @param myBeanDefinition
     * @return
     */
    private Object instantiateBean(MyBeanDefinition myBeanDefinition) {
        Object instance = null;
        // 获取到改bean的全名
        String className = myBeanDefinition.getBeanClassName();
        try{
            // 判断缓存中是否存在改bean的实例
            if (this.factoryBeanObjectCache.containsKey(className)) {
                instance = this.factoryBeanObjectCache.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                // 将新实例化的bean放入缓存
                this.factoryBeanObjectCache.put(myBeanDefinition.getFactoryBeanName(), instance);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getName());
    }
}
