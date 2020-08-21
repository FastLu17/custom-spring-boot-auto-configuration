package com.luxf.custom.classloader;

import com.luxf.custom.config.IProperties;
import com.luxf.custom.entity.User;
import org.springframework.context.annotation.LoadTimeWeavingConfiguration;
import org.springframework.context.annotation.LoadTimeWeavingConfigurer;
import org.springframework.context.weaving.DefaultContextLoadTimeWeaver;
import org.springframework.context.weaving.LoadTimeWeaverAware;
import org.springframework.context.weaving.LoadTimeWeaverAwareProcessor;
import org.springframework.core.DecoratingClassLoader;
import org.springframework.core.OverridingClassLoader;
import org.springframework.instrument.classloading.LoadTimeWeaver;

/**
 * Spring中自定义的ClassLoader包含{@link DecoratingClassLoader}的多个实现类、
 * 主要是{@link OverridingClassLoader,org.springframework.context.support.ContextTypeMatchClassLoader}
 * <p>
 * TODO: ContextTypeMatchClassLoader用于{@link LoadTimeWeaver}AOP类加载期间织入、
 * 有关 load-time-weaver 的对象简单标注：
 * 1、{@link org.springframework.context.config.LoadTimeWeaverBeanDefinitionParser}
 * 2、{@link LoadTimeWeaverAwareProcessor,LoadTimeWeaverAware,LoadTimeWeavingConfiguration,LoadTimeWeavingConfigurer,DefaultContextLoadTimeWeaver}
 * <p>
 * 在{@link LoadTimeWeavingConfiguration#loadTimeWeaver()}中注入Bean对象{@link LoadTimeWeaver}时,
 * 先判断是否有自定义的{@link LoadTimeWeavingConfigurer}、否则使用{@link DefaultContextLoadTimeWeaver}。
 *
 * <p>
 * Class.forName()和ClassLoader.loadClass()的区别：
 * 1、Class.forName(name)：会初始化类。(类要创建实例需要先加载并初始化该类、即静态成员变量和静态代码块的初始化)
 * 2、ClassLoader.loadClass(name)：不会初始化类。
 * 3、loadClass(name, true)：会初始化类。(受到第二个参数的影响)
 * <p>
 * Spring IOC大量使用到延迟加载技术,因此选择ClassLoader.loadClass(),可以加快初始化速度。
 *
 * @author 小66
 * @date 2020-08-17 10:05
 **/
public class SpringClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        ClassLoader threadLoader = Thread.currentThread().getContextClassLoader();
        Class<?> aClass = Class.forName(User.class.getName());
        OverridingClassLoader overridingLoader = new OverridingClassLoader(threadLoader);
        // 排除User,表示使用JDK的双亲委派机制加载、
        /**
         *  解释{@link OverridingClassLoader#loadClass(String)}方法、
         *
         *  @Override
         * 	public Class<?> loadClass(String name) throws ClassNotFoundException {
         * 	    // 创建OverridingClassLoader对象时,没有传递overrideDelegate参数,则此处判断为false、
         * 		if (this.overrideDelegate != null && isEligibleForOverriding(name)) {
         * 			return this.overrideDelegate.loadClass(name);
         * 		}
         * 	    // 	super.loadClass(name);直接调用了loadClass(String name, boolean resolve)。由于子类重写了该方法
         * 		return super.loadClass(name);
         * 	}
         *
         *  // super.loadClass(name)最终会进入该方法。
         * 	@Override
         * 	protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
         * 	    // 判断该类是否需要重新加载、即{@link DecoratingClassLoader#excludedPackages,excludedClasses}是否包含该类、
         * 		if (isEligibleForOverriding(name)) {
         * 	        // 该方法内,会重新加载Class对象、
         * 			Class<?> result = loadClassForOverriding(name);
         * 			if (result != null) {
         * 				if (resolve) {
         * 					resolveClass(result);
         * 				}
         * 				return result;
         * 			}
         * 		}
         * 		return super.loadClass(name, resolve);
         * 	}
         */
        overridingLoader.excludeClass(User.class.getName());

        // 添加到 excludedPackages 或 excludedClasses 的类就不会被代理的 ClassLoader 加载
        // 而会使用 JDK 默认的双亲委派机制。
        // 因此 User 不会被 OverridingClassLoader重新加载,而 IProperties 会重新加载。
        Class<?> threadLoadClass = threadLoader.loadClass(User.class.getName());
        Class<?> overridingLoadClass = overridingLoader.loadClass(User.class.getName());
        // true、
        System.out.println("overridingLoadClass==threadLoadClass = " + (overridingLoadClass == threadLoadClass));

        Class<?> nonExcludeThreadLoadClass = threadLoader.loadClass(IProperties.class.getName());
        Class<?> nonExcludeOverridingLoadClass = overridingLoader.loadClass(IProperties.class.getName());
        // false、
        System.out.println("nonExcludeThreadLoadClass==nonExcludeOverridingLoadClass = " + (nonExcludeThreadLoadClass == nonExcludeOverridingLoadClass));
    }
}
