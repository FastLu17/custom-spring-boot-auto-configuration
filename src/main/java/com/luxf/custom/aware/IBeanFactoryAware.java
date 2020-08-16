package com.luxf.custom.aware;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author 小66
 * @date 2020-08-16 22:14
 **/
@Component
public class IBeanFactoryAware implements BeanFactoryAware {
    private static BeanFactory beanFactory;

    /**
     * {@link DefaultListableBeanFactory}有实现更多对Bean操作的方法。
     *
     * @param beanFactory 默认情况下是DefaultListableBeanFactory实现的BeanFactory接口、
     * @throws BeansException
     */
    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        IBeanFactoryAware.beanFactory = beanFactory;
    }

    public static BeanFactory getBeanFactory() {
        return beanFactory;
    }
}
