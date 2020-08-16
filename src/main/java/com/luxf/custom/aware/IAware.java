package com.luxf.custom.aware;

import org.springframework.beans.factory.Aware;
import org.springframework.beans.factory.BeanClassLoaderAware;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.*;
import org.springframework.context.support.AbstractApplicationContext;

/**
 * {@link Aware}接口没有方法、是空接口！
 * {@link org.springframework.context.support.ApplicationContextAwareProcessor}该类比较特殊、此类会处理6个Aware接口的实现类的实现方法。
 *
 * @author 小66
 * @date 2020-08-16 20:11
 **/
public class IAware implements Aware {

    /**
     * 无所不知的Aware接口：
     * Aware类型的接口的作用就是让我们能够拿到Spring容器中的一些资源。基本都能够见名知意，Aware接口名字的前缀就是可以拿到具体的资源。
     * 例如BeanNameAware可以拿到BeanName，以此类推。TODO 调用时机需要注意：所有的Aware方法都是在初始化阶段之前调用的！
     * Spring核心的Aware接口众多。
     * 根据源码内处理AWare接口的方法,将Aware接口具体可以分为两组。如下排列顺序同样也是Aware接口的执行顺序。
     *
     * 第一组：
     *  1、{@link BeanNameAware}
     *  2、{@link BeanClassLoaderAware}
     *  3、{@link BeanFactoryAware}
     *
     * 第二组：
     *  1、{@link EnvironmentAware}
     *  2、{@link EmbeddedValueResolverAware}：实现该接口能够获取Spring EL解析器，用户的自定义注解需要支持SPEL表达式的时候可以使用。
     *  3、{@link ResourceLoaderAware}
     *  4、{@link ApplicationEventPublisherAware}
     *  5、{@link MessageSourceAware}
     *  6、{@link ApplicationContextAware}
     *
     * TODO: 以下4个接口都有类似的注释： the MessageSource (typically{通常、一般} the ApplicationContext)
     * ResourceLoaderAware、ApplicationEventPublisherAware、MessageSourceAware、ApplicationContextAware这4个接口都是ApplicationContext、
     *
     * {@link ApplicationContext} public interface ApplicationContext extends EnvironmentCapable, ListableBeanFactory, HierarchicalBeanFactory,
     * 		MessageSource, ApplicationEventPublisher, ResourcePatternResolver{}
     * 	TODO: 关于ApplicationContext的定义：继承2个BeanFactory的接口、其他4个接口就是关联以上4个Aware的。
     *
     * {@link AbstractApplicationContext#prepareBeanFactory(ConfigurableListableBeanFactory)}该方法内初始化ApplicationContextAwareProcessor对象、
     * protected void prepareBeanFactory(ConfigurableListableBeanFactory beanFactory) {
     *      ...
     *
     * 		// Configure the bean factory with context callbacks.
     * 	    // TODO: 此时注册添加ApplicationContextAwareProcessor,会在实例化Bean对象时,处理以下6个接口的实现类的方法、
     * 		beanFactory.addBeanPostProcessor(new ApplicationContextAwareProcessor(this));
     * 		beanFactory.ignoreDependencyInterface(EnvironmentAware.class);
     * 		beanFactory.ignoreDependencyInterface(EmbeddedValueResolverAware.class);
     * 		beanFactory.ignoreDependencyInterface(ResourceLoaderAware.class);
     * 		beanFactory.ignoreDependencyInterface(ApplicationEventPublisherAware.class);
     * 		beanFactory.ignoreDependencyInterface(MessageSourceAware.class);
     * 		beanFactory.ignoreDependencyInterface(ApplicationContextAware.class);
     *
     *      ...
     * }
     *
     * {@link AbstractAutowireCapableBeanFactory#initializeBean(String, Object, RootBeanDefinition)}该方法内部分别处理以上2组Aware接口。
     * protected Object initializeBean(final String beanName, final Object bean, @Nullable RootBeanDefinition mbd) {
     * 		if (System.getSecurityManager() != null) {
     * 			AccessController.doPrivileged((PrivilegedAction<Object>) () -> {
     * 				invokeAwareMethods(beanName, bean);
     * 				return null;
     *                        }, getAccessControlContext());* 		}
     * 		else {
     * 	        // TODO: 处理BeanNameAware、BeanClassLoaderAware、BeanFactoryAware的实现类
     *          @see AbstractAutowireCapableBeanFactory#invokeAwareMethods(String, Object)
     * 			invokeAwareMethods(beanName, bean);
     *        }
     *
     * 		Object wrappedBean = bean;
     * 		if (mbd == null || !mbd.isSynthetic()) {
     * 	        // 处理BeanPostProcessor接口的postProcessBeforeInitialization()方法
     * 	        // TODO：如果当前BeanFactory对象的beanPostProcessors属性中包含ApplicationContextAwareProcessor时,则会执行到ApplicationContextAwareProcessor#invokeAwareInterfaces()方法、
     *          @see org.springframework.context.support.ApplicationContextAwareProcessor#invokeAwareInterfaces(Object)
     * 			wrappedBean = applyBeanPostProcessorsBeforeInitialization(wrappedBean, beanName);
     *        }
     *
     * 		try {
     * 	        // TODO: 处理InitializingBean接口的afterPropertiesSet()方法
     *          @see AbstractAutowireCapableBeanFactory#invokeInitMethods(String, Object, RootBeanDefinition)
     * 			invokeInitMethods(beanName, wrappedBean, mbd);
     *        }
     * 		catch (Throwable ex) {
     * 			throw new BeanCreationException(
     * 					(mbd != null ? mbd.getResourceDescription() : null),
     * 					beanName, "Invocation of init method failed", ex);
     *        }
     * 		if (mbd == null || !mbd.isSynthetic()) {
     * 	        // TODO: 处理BeanPostProcessor接口的postProcessAfterInitialization()方法
     * 			wrappedBean = applyBeanPostProcessorsAfterInitialization(wrappedBean, beanName);
     *        }
     *
     * 		return wrappedBean;
     *    }
     */
}
