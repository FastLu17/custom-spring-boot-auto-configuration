package com.luxf.custom.config;

import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.AbstractBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Component;

/**
 * {@link AbstractAutowireCapableBeanFactory#resolveBeforeInstantiation(String, RootBeanDefinition)}该方法内
 * 会调用{@link InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation(Class, String)}实例化前置方法的实现、
 * <p>
 * {@link AbstractAutowireCapableBeanFactory#initializeBean(String, Object, RootBeanDefinition)}该方法内部会
 * 调用{@link BeanPostProcessor}接口的2个生命周期方法、
 * <p>
 * TODO: {@link InstantiationAwareBeanPostProcessor}接口继承了{@link BeanPostProcessor}接口、如果需要处理全部的生命周期,直接实现{@link InstantiationAwareBeanPostProcessor}即可、
 * <p>
 * Spring Bean的生命周期只有这四个阶段：实例化 -> 属性赋值 -> 初始化 -> 销毁
 * 1、实例化  Instantiation
 * 2、属性赋值  Populate
 * 3、初始化  Initialization
 * 4、销毁  Destruction
 * TODO: 实例化、属性赋值、初始化三个阶段的主要逻辑都在{@link AbstractAutowireCapableBeanFactory#doCreateBean(String, RootBeanDefinition, Object[])}中实现、
 *
 * @author 小66
 * @date 2020-08-15 15:17
 **/
@Component
public class LifeCiycleBean implements InstantiationAwareBeanPostProcessor, BeanPostProcessor, InitializingBean, DisposableBean {

    /**
     * 1、最先调用beforeInstantiation
     *
     * {@link AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInstantiation(Class, String)}}内调用该方法、
     * 执行实例化前操作、
     *
     * TODO: 没有特殊要求,不要实现这个方法、
     * Spring框架下,只有{@link ScriptFactoryPostProcessor,AbstractAutoProxyCreator}实现这个方法时没有默认返回null。
     *
     * 如果这个方法,没有返回null、则直接会调用{@link BeanPostProcessor#postProcessAfterInitialization(Object, String)}.
     * @param beanClass
     * @param beanName
     * @return
     * @throws BeansException
     */
//    @Override
//    public Object postProcessBeforeInstantiation(Class<?> beanClass, String beanName) throws BeansException {
//        return null;
//    }

    /**
     * 2、接着调用afterInstantiation
     * {@link AbstractAutowireCapableBeanFactory#populateBean(String, RootBeanDefinition, BeanWrapper)}内调用该方法、
     * 执行实例化后操作、
     *
     * @return 如果应在bean上设置属性，则为true；否则为false。TODO: 返回false还将阻止任何后续的InstantiationAwareBeanPostProcessor实例对此bean实例调用。
     */
    @Override
    public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
        // TODO: 如果返回false、导致整个项目无法启动！并且出现 IllegalArgumentException: Property 'sqlSessionFactory' or 'sqlSessionTemplate' are required。
        return true;
    }

    /**
     * 3、接着调用postProcessProperties、 TODO：为bean对象的属性赋值.
     * {@link AbstractAutowireCapableBeanFactory#applyPropertyValues(String, BeanDefinition, BeanWrapper, PropertyValues)} 会调用该方法、
     */
    @Override
    public PropertyValues postProcessProperties(PropertyValues pvs, Object bean, String beanName) throws BeansException {
        if (pvs != null && !pvs.isEmpty()) {
            System.out.println("PropertyValues = " + pvs);
        }
        return pvs;
    }

    /**
     * {@link AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsBeforeInitialization(Object, String)}方法内部会调用该方法、
     */
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * 实现了{@link BeanPostProcessor}接口的Bean对象,就会被添加到{@link AbstractBeanFactory#beanPostProcessors}中.
     * {@link AbstractAutowireCapableBeanFactory#applyBeanPostProcessorsAfterInitialization(Object, String)} (Object, String)}方法内部会调用该方法、
     * 对{@link AbstractBeanFactory#getBeanPostProcessors()}结果进行遍历. TODO：每个被实例化的Bean对象,都会被所有的{@link BeanPostProcessor}处理！
     * 类似{@link AbstractAutoProxyCreator#postProcessAfterInitialization(java.lang.Object, java.lang.String)} 判断是否需要创建动态代理的对象.
     *
     * TODO：同样的alibaba seata中的GlobalTransactionScanner继承并重写{@link AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)}方法,自定义动态代理规则。
     */
    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    /**
     * {@link InitializingBean}接口的实现、
     * {@link AbstractAutowireCapableBeanFactory#invokeInitMethods(String, Object, RootBeanDefinition)}该方法内部调用、
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        // 相当于执行postProcessProperties()后、即实例化对象后的生命周期方法。
    }

    /**
     * {@link DisposableBean}接口的实现、
     * 由{@link ConfigurableApplicationContext#close()}调用
     */
    @Override
    public void destroy() throws Exception {
        // 对应生命周期的销毁阶段，以ConfigurableApplicationContext#close()方法作为入口。
        // 实现是通过循环取所有实现了DisposableBean接口的Bean然后调用其destroy()方法。
    }
}
