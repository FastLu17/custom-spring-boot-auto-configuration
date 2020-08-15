package com.luxf.custom.helper;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 获取Spring容器中的任意Bean、
 * 必须添加到容器中去、
 * <p>
 * 注意：通过 ApplicationContextHelper 在 其他的Bean@PostConstruct的初始化方法中时、获取到的application是null、
 *
 * @author 小66
 */
@Component
public class ApplicationContextHelper implements ApplicationContextAware, BeanPostProcessor {


    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        ApplicationContextHelper.applicationContext = applicationContext;
    }

    /**
     * 获取spring容器中的bean, 通过bean类型获取
     *
     * @param beanClass Bean的class对象
     * @return T 返回指定类型的bean实例
     */
    public static <T> T getBean(Class<T> beanClass) {
        return applicationContext.getBean(beanClass);
    }

    public static <T> T getBean(String beanName, Class<T> beanClass) {
        return applicationContext.getBean(beanName, beanClass);
    }

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}