package com.luxf.custom.aware;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * 当一个Bean对象实现{@link ApplicationContextAware}接口后，这个类就可以方便地获得 ApplicationContext 中的所有bean。
 * <p>
 * Spring容器会检测容器中的所有Bean，如果发现某个Bean实现了ApplicationContextAware接口，
 * Spring容器会在初始化该Bean时，自动调用{@link org.springframework.context.support.ApplicationContextAwareProcessor#postProcessBeforeInitialization(Object, String)}方法，
 * 该方法内会调用{@link org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces(Object)}。
 *
 * TODO: invokeAwareInterfaces()方法会处理,以下6个Aware接口的实现类、因此可以实现这6个接口,进行相关操作！
 * private void invokeAwareInterfaces(Object bean) {
 * 		if (bean instanceof Aware) {
 * 			if (bean instanceof EnvironmentAware) {
 * 				((EnvironmentAware) bean).setEnvironment(this.applicationContext.getEnvironment());
 *                        }
 * 			if (bean instanceof EmbeddedValueResolverAware) {
 * 				((EmbeddedValueResolverAware) bean).setEmbeddedValueResolver(this.embeddedValueResolver);
 *            }
 * 			if (bean instanceof ResourceLoaderAware) {
 * 				((ResourceLoaderAware) bean).setResourceLoader(this.applicationContext);
 *            }
 * 			if (bean instanceof ApplicationEventPublisherAware) {
 * 				((ApplicationEventPublisherAware) bean).setApplicationEventPublisher(this.applicationContext);
 *            }
 * 			if (bean instanceof MessageSourceAware) {
 * 				((MessageSourceAware) bean).setMessageSource(this.applicationContext);
 *            }
 *          //TODO: 处理ApplicationContextAware的实现类、
 * 			if (bean instanceof ApplicationContextAware) {
 * 				((ApplicationContextAware) bean).setApplicationContext(this.applicationContext);
 *            }
 *      }
 * 	}
 *
 * @author 小66
 * @date 2020-08-16 19:21
 **/
@Component
public class IApplicationContextAware implements ApplicationContextAware {

    private static ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        IApplicationContextAware.applicationContext = applicationContext;
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

    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }
}
