package com.luxf.custom.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.aop.Advisor;
import org.springframework.aop.PointcutAdvisor;
import org.springframework.aop.SpringProxy;
import org.springframework.aop.TargetSource;
import org.springframework.aop.aspectj.AbstractAspectJAdvice;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.aspectj.DeclareParentsAdvisor;
import org.springframework.aop.aspectj.annotation.*;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.aop.framework.*;
import org.springframework.aop.framework.adapter.AdvisorAdapter;
import org.springframework.aop.framework.autoproxy.AbstractAdvisorAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.aop.support.AopUtils;
import org.springframework.aop.target.SingletonTargetSource;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.AbstractAutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.interceptor.BeanFactoryCacheOperationSourceAdvisor;
import org.springframework.cglib.proxy.Enhancer;
import org.springframework.cglib.proxy.MethodProxy;
import org.springframework.context.annotation.*;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncAnnotationAdvisor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.BeanFactoryTransactionAttributeSourceAdvisor;
import org.springframework.transaction.interceptor.TransactionInterceptor;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Set;

/**
 * {@link BeanFactoryCacheOperationSourceAdvisor} 对应：{@link Cacheable}系列的注解进行AOP拦截、
 * {@link BeanFactoryTransactionAttributeSourceAdvisor} 对应：{@link Transactional}注解进行AOP拦截、
 * {@link AsyncAnnotationAdvisor} 对应：{@link Async}注解进行AOP拦截、
 * {@link org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl} 对应：匹配到{@link Aspect}对应规则的方法/类进行AOP拦截、
 *
 * 以上四个Advisor都继承于{@link PointcutAdvisor}接口
 *
 * Spring 动态代理：由{@link EnableAspectJAutoProxy}作为入口，可以配置CGLIB或者JDK动态代理。
 * 实现{@link EnableAspectJAutoProxy}主要是依靠{@link AspectJAutoProxyRegistrar}, {@link ImportBeanDefinitionRegistrar}接口的实现类、
 * <p>
 * 方法调用链：
 * 可以由{@link org.springframework.context.support.AbstractApplicationContext#invokeBeanFactoryPostProcessors(ConfigurableListableBeanFactory)}
 * 内部调用{@link ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)},
 * 走到{@link ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(Set)},
 * 最终调用{@link ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsFromRegistrars(Map)}处理{@link ImportBeanDefinitionRegistrar}接口的各种实现、
 *
 * {@link AspectJAutoProxyRegistrar#registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)} 具体实现解析：
 * // TODO: 此处只是将{@link AnnotationAwareAspectJAutoProxyCreator}自动注册到Spring容器中、 产生一个Name为{@link AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME}的对象、
 * //注释内容：根据导入的{Configuration}类上的{EnableAspectJAutoProxy＃proxyTargetClass（）}属性的值，注册，升级和配置AspectJ自动代理创建器。
 * public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
 *      // 注册或者升级。该方法内先判断是否存在指定Name的Bean对象、如果存在就校验是否需要升级, 否则就注册Name为的{@link BeanDefinition}对象、
 * 		AopConfigUtils.registerAspectJAnnotationAutoProxyCreatorIfNecessary(registry);
 *
 *      // 获取{@link Configuration}的Bean对象上的{@link EnableAspectJAutoProxy}注解信息、
 * 		AnnotationAttributes enableAspectJAutoProxy =
 * 				AnnotationConfigUtils.attributesFor(importingClassMetadata, EnableAspectJAutoProxy.class);
 * 		if (enableAspectJAutoProxy != null) {
 * 	        // {@link EnableAspectJAutoProxy#proxyTargetClass()}的结果为{@code true},
 * 	        // 则为注册的{@link AopConfigUtils#AUTO_PROXY_CREATOR_BEAN_NAME}Bean对象添加{@code proxyTargetClass}属性、
 * 			if (enableAspectJAutoProxy.getBoolean("proxyTargetClass")) {
 * 				AopConfigUtils.forceAutoProxyCreatorToUseClassProxying(registry);
 *          }
 *          // 同样是添加{@code exposeProxy}属性
 * 			if (enableAspectJAutoProxy.getBoolean("exposeProxy")) {
 * 				AopConfigUtils.forceAutoProxyCreatorToExposeProxy(registry);
 *          }
 *		}
 * }
 *
 * 关于{@link EnableAspectJAutoProxy#exposeProxy()}属性缘由配置的解析：
 * 在Spring中,首先调用的是AOP代理对象而不是目标对象，首先执行事务切面。
 * 事务切面内部通过{@link TransactionInterceptor}环绕增强进行事务的增强，即进入目标方法之前开启事务，退出目标方法时提交/回滚事务。
 *
 * @Service
 * public class UserServiceImpl implements UserService{
 *      @Transactional(propagation = Propagation.REQUIRED)
 *      public void a() {
 *          // 目标对象内部的自我调用将无法实施切面中的增强。即a()的事务正常,b()的事务失效、
 *          // 因为a()中调用b()属于内部调用，没有通过代理，所以不会有事务产生。
 *          // 通过设置{@link EnableAspectJAutoProxy#exposeProxy()}属性为true，将代理暴露出来，使用AopContext.currentProxy()获取当前代理。
 *          // {@link AopContext#currentProxy()}可以获取到当前对象的代理对象、
 *          // {@link AopContext#currentProxy},该属性是{@link ThreadLocal}对象,当前线程内共享同一个代理对象。
 *          // TODO: ThreadLocal的使用示例、
 *          this.b();
 *          // ((UserService)AopContext.currentProxy()).b(); 这样使用,b()的事务可以生效、
 *      }
 *      @Transactional(propagation = Propagation.REQUIRES_NEW)
 *      public void b() {
 *          // do something.
 *      }
 * }
 *
 * <p>
 * 在{@link AopProxy}接口的2个实现类,{@link CglibAopProxy}(该对象存些许多的内部类实现了拦截器)和{@link JdkDynamicAopProxy}中的intercept()/invoke()方法,
 * 都使用到了{@link AopContext#setCurrentProxy(Object)},用于对线程内的代理对象进行初始化、
 *
 * 从Spring 4.0起, {@link ObjenesisCglibAopProxy}作为CglibAopProxy的默认实现。
 * {@link AopUtils#isAopProxy(Object)}提供几个判断是否为代理类的方法、
 *
 * TODO：何处执行创建代理类的操作？ {@link AbstractAutowireCapableBeanFactory#initializeBean(String, Object, RootBeanDefinition)} 593行、
 * {@link AnnotationAwareAspectJAutoProxyCreator}的父类{@link AbstractAutoProxyCreator}实现了{@link BeanPostProcessor}接口(初始化,实例化)、
 *
 * 在{@link AbstractAutoProxyCreator#postProcessAfterInitialization(Object, String)}方法中,判断该Bean对象是否需要代理、
 * // 注释内容：如果Bean被子类标识为要代理的bean，则使用配置的拦截器创建代理。
 * public Object postProcessAfterInitialization(@Nullable Object bean, String beanName) {
 * 		if (bean != null) {
 * 			Object cacheKey = getCacheKey(bean.getClass(), beanName);
 * 			if (this.earlyProxyReferences.remove(cacheKey) != bean) {
 * 		        // 如果需要,就对当前bean进行封装、{@link AbstractAutoProxyCreator#wrapIfNecessary(Object, String, Object)}
 * 		        // 在该方法内主要做了三件事：
 * 		        1、判断当前bean是否已经生成过代理对象，或者是否是应该被略过的对象，是则直接返回，否则进行下一步;
 *              2、获取当前bean的Advisors和Advices，如果当前bean不需要代理，则返回DO_NOT_PROXY;
 *              3、通过生成的Advisors和Advices为目标bean生成代理对象,实际是Advisor的List。
 *              TODO：核心方法解析{@link org.springframework.aop.aspectj.annotation.InstantiationModelAwarePointcutAdvisorImpl},
 *                  {@link BeanFactoryCacheOperationSourceAdvisor,BeanFactoryTransactionAttributeSourceAdvisor,AsyncAnnotationAdvisor}等核心的Advisor、
 *              @see AbstractAdvisorAutoProxyCreator#getAdvicesAndAdvisorsForBean 获取当前bean的Advices和Advisors。
 *
 *              {@link AnnotationAwareAspectJAutoProxyCreator#findCandidateAdvisors()}：先寻找所有实现{@link Advisor}接口的实现类, 再寻找{@link Aspect}的Bean
 *              // TODO：给符合条件的Bean创建代理对象、
 * 				return wrapIfNecessary(bean, beanName, cacheKey);
 * 			}
 * 		}
 * 		return bean;
 * 	}
 *
 * 	TODO: 真正创建代理对象的流程、 源码解析
 * 	{@link AbstractAutoProxyCreator#createProxy(Class, String, Object[], TargetSource)}
 *  protected Object createProxy(Class<?> beanClass, @Nullable String beanName, Object[] specificInterceptors, TargetSource targetSource) {
 *
 * 		// 设置属性、
 * 		if (this.beanFactory instanceof ConfigurableListableBeanFactory) {
 * 			AutoProxyUtils.exposeTargetClass((ConfigurableListableBeanFactory) this.beanFactory, beanName, beanClass);
 * 		}
 *
 * 		ProxyFactory proxyFactory = new ProxyFactory();
 * 		proxyFactory.copyFrom(this);
 *
 *      // proxyFactory.isProxyTargetClass() ->  默认是 true, 使用CGLIB动态代理
 * 		if (!proxyFactory.isProxyTargetClass()) {
 * 			if (shouldProxyTargetClass(beanClass, beanName)) {
 * 				proxyFactory.setProxyTargetClass(true);
 * 			}
 * 			else {
 * 				evaluateProxyInterfaces(beanClass, proxyFactory);
 * 			}
 * 		}
 *
 *      // specificInterceptors本来就是Advisor的数组, buildAdvisors()主要是添加通用的Advisor, 并将其包装为Advisor、
 *      // 将拦截器封装为增强器、
 * 		Advisor[] advisors = buildAdvisors(beanName, specificInterceptors);
 * 		proxyFactory.addAdvisors(advisors);
 *
 * 	    // TODO: {@link AbstractAutoProxyCreator#postProcessBeforeInstantiation(Class, String)}方法中,会加载自定义的{@link TargetSource}
 * 	    // {@link TargetSource}可以进行自定义、此处默认使用{@link SingletonTargetSource}.
 * 		proxyFactory.setTargetSource(targetSource);
 * 	    // 空实现、可以自定义部分内容, 定制代理、
 * 		customizeProxyFactory(proxyFactory);
 *
 *      // 用来控制代理工厂被配置之后,是否还允许修改通知。默认 false.(被代理后,不允许修改代理的配置)
 * 		proxyFactory.setFrozen(this.freezeProxy);
 * 		if (advisorsPreFiltered()) {
 * 			proxyFactory.setPreFiltered(true);
 * 		}
 *      // 关于{@link ProxyFactory#getProxy(ClassLoader)}方法的解读
 *      // 1、先调用{@link DefaultAopProxyFactory#createAopProxy(AdvisedSupport)} 创建CGLIB/JDK的{@link AopProxy}对象、
 *      // 2、然后在调用{@link AopProxy#getProxy(ClassLoader)}方法、比如{@link CglibAopProxy#getProxy(ClassLoader)}
 *
 *      // TODO：创建代理对象后,调用代理对象的方法时, 比如{@link JdkDynamicAopProxy#invoke(Object, Method, Object[])}就会生效、
 *      // 而CglibAopProxy主要是利用内部类{@link CglibAopProxy.DynamicAdvisedInterceptor#intercept(Object, Method, Object[], MethodProxy)}的拦截器进行对应的拦截、
 * 		return proxyFactory.getProxy(getProxyClassLoader());
 * 	}
 *
 * 	TODO: 关于{@link CglibAopProxy#getProxy(ClassLoader)}如何利用{@link Enhancer}创建真正的代理对象、
 * 	public Object getProxy(@Nullable ClassLoader classLoader) {
 * 		try {
 * 			Class<?> rootClass = this.advised.getTargetClass();
 *
 * 			Class<?> proxySuperClass = rootClass;
 * 		    // 判断被代理对象是否为CGLIB代理类、
 * 			if (ClassUtils.isCglibProxyClass(rootClass)) {
 * 				proxySuperClass = rootClass.getSuperclass();
 * 				Class<?>[] additionalInterfaces = rootClass.getInterfaces();
 * 				for (Class<?> additionalInterface : additionalInterfaces) {
 * 					this.advised.addInterface(additionalInterface);
 * 				}
 * 			}
 *
 * 			validateClassIfNecessary(proxySuperClass, classLoader);
 *
 * 			// 配置 CGLIB Enhancer 对象.
 * 			// TODO: {@link org.springframework.cglib.proxy.Enhancer}, 不是{net.sf.cglib.proxy.Enhancer}
 * 			Enhancer enhancer = createEnhancer();
 * 			if (classLoader != null) {
 * 				enhancer.setClassLoader(classLoader);
 * 				if (classLoader instanceof SmartClassLoader &&
 * 						((SmartClassLoader) classLoader).isClassReloadable(proxySuperClass)) {
 * 					enhancer.setUseCache(false);
 * 				}
 * 			}
 * 		    // 指定被代理的类
 * 			enhancer.setSuperclass(proxySuperClass);
 * 		    // TODO: 这里只能获取到2个接口{@link SpringProxy,Advised}、即只代理这2个接口.
 * 			enhancer.setInterfaces(AopProxyUtils.completeProxiedInterfaces(this.advised));
 *          // 命名政策
 * 			enhancer.setNamingPolicy(SpringNamingPolicy.INSTANCE);
 *
 * 			enhancer.setStrategy(new ClassLoaderAwareUndeclaredThrowableStrategy(classLoader));
 *
 *          // TODO：完成拦截器的核心地方、设置拦截器链
 *          // {@link CglibAopProxy#getCallbacks(Class)} 将当前类，注册到{@link CglibAopProxy}多个内部类(拦截器)中.
 * 			Callback[] callbacks = getCallbacks(rootClass);
 * 			Class<?>[] types = new Class<?>[callbacks.length];
 * 			for (int x = 0; x < types.length; x++) {
 * 				types[x] = callbacks[x].getClass();
 * 			}
 *
 * 			// fixedInterceptorMap only populated(赋值、填充) at this point, after getCallbacks() call.
 * 			enhancer.setCallbackFilter(new ProxyCallbackFilter(
 * 					this.advised.getConfigurationOnlyCopy(), this.fixedInterceptorMap, this.fixedInterceptorOffset));
 *
 * 		    // 将拦截器绑定到Enhancer上.
 * 			enhancer.setCallbackTypes(types);
 *
 * 			// 生成代理类并创建代理实例。
 * 			return createProxyClassAndInstance(enhancer, callbacks);
 * 		}
 * 		catch (Exceptionex) {
 * 			throw new AopConfigException("Unexpected AOP exception", ex);
 * 		}
 * 	}
 *
 * @author 小66
 * @date 2020-08-19 19:36
 **/
@Aspect
@Component
public class IAspectJ {
    /**
     * TODO: 在{@link Aspect}的Bean对象中,如果有以"ajc$"开头的字段名,则这个Bean不会起到AOP拦截的作用！
     * <p>
     * {@link BeanFactoryAspectJAdvisorsBuilder#buildAspectJAdvisors()}该方法内会处理{@link Aspect}相关的内容。
     *
     * 由 this.advisorFactory.getAdvisors(factory)进入，最终调用至此getAdvice()方法。
     * {@link ReflectiveAspectJAdvisorFactory#getAdvice(Method, AspectJExpressionPointcut, MetadataAwareAspectInstanceFactory, int, String)}
     * TODO: 该方法里为{@link Pointcut,Around,Before,After,AfterThrowing,AfterReturning}注解的方法分别创建不同的{@link AbstractAspectJAdvice}实现类的实例对象、
     * 对于不同的实例,有些是直接实现{@link MethodInterceptor}接口,有些是通过{@link AdvisorAdapter}间接实现{@link MethodInterceptor}接口、
     *
     * @see DeclareParents 如果带有{@link Aspect}注解的Bean对象的field上存在该注解,则会构建一个{@link DeclareParentsAdvisor}。
     * @see AspectJExpressionPointcut 该对象存储着{@link Aspect}相关的 PointCutExpression、
     * @see AbstractAspectJAdvisorFactory#AJC_MAGIC
     * @see AbstractAspectJAdvisorFactory#compiledByAjc(Class)
     * @see AbstractAspectJAdvisorFactory#ASPECTJ_ANNOTATION_CLASSES 关于{@link Aspect}的Bean对象方法上的6个注解、
     */
//    private static String ajc$filed = "AOP_NO_EFFECT";

    @Pointcut("execution(* com.luxf..*.service.*.select*(..))")
    public void pointCut() {
    }

    @Around("pointCut()")
    public Object doAround(ProceedingJoinPoint pjp) {
        Object result = null;
        try {
            System.out.println("Before Invoke Method.");
            result = pjp.proceed();
            System.out.println("After Invoke Method.");
        } catch (Throwable e) {
            e.printStackTrace();
        }
        System.out.println("result = " + result);
        return result;
    }
}
