package com.luxf.custom.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessor;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * 自定义的方式、动态注册Bean对象、
 * <p>
 * Bean定义注册后置处理器在{@link AbstractApplicationContext#refresh()}内部处理、
 * 通过方法链 (refresh() -> invokeBeanFactoryPostProcessors() ->
 * PostProcessorRegistrationDelegate.invokeBeanFactoryPostProcessors() ->
 * PostProcessorRegistrationDelegate.invokeBeanDefinitionRegistryPostProcessors()) 调用。
 *
 * @author 小66
 * @date 2020-08-15 21:00
 * @see org.springframework.context.support.PostProcessorRegistrationDelegate#invokeBeanDefinitionRegistryPostProcessors(Collection, BeanDefinitionRegistry)
 **/
@Component
public class IBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

    /**
     * 该方法用来注册更多的bean到spring容器中，详细观察入参{@link BeanDefinitionRegistry}接口。
     * 可以直接使用入参registry进行注册、移除等操作
     */
    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        // IProperties是非容器对象、
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(IProperties.class);
        // 为IProperties对象的属性赋值、
        builder.addPropertyValue("port", "8080");
        // 把IProperties注册到Spring容器中、可以直接通过@Autowire使用、
        AbstractBeanDefinition definition = builder.getBeanDefinition();
        registry.registerBeanDefinition(definition.getBeanClassName(), definition);

        /**
         * {@link ClassPathScanningCandidateComponentProvider}(该类有多个实现)批量自定义注册Bean、
         *
         * {@link MapperScan}的实现就使用到{@link ClassPathBeanDefinitionScanner}
         */
        // 是否使用默认的filter，使用默认的filter意味着只扫描那些类上拥有Component、Service、Repository或Controller注解的类。
        boolean useDefaultFilters = false;
        ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(useDefaultFilters);
        // 根据自定义注解来过滤、
        TypeFilter filter = new AnnotationTypeFilter(Table.class);
        scanner.addIncludeFilter(filter);
        Set<BeanDefinition> beanDefinitions = scanner.findCandidateComponents("com.luxf");
        beanDefinitions.forEach(bean -> registry.registerBeanDefinition(bean.getBeanClassName(), bean));
    }

    /**
     * {@link BeanFactoryPostProcessor}的方法、正常不用处理.该方法的实现中,主要用来对bean定义做一些改变。
     * <p>
     * TODO: 此处实现{@link BeanFactoryPostProcessor}的方法时, 不要使用beanFactory.getBeansXXX()等类似的方法, 否则会导致Bean提前实例化/初始化, 无法正常完成依赖注入、 导致@Autowire/@Resource的Bean为null、
     * <p>
     * 在使用spring的BeanPostProcessor和BeanFactoryPostProcessor的时候,
     * 在spring的初始化过程中, 提前将还不打算实例化的bean, 提前实例化了, 这些实例化的BEAN, 其实还有依赖一些配置或者BEAN未正常配置完成, 从而引发问题!
     * <p>
     * {@link ConfigurableListableBeanFactory}的getBeanNamesXXX()系列的方法是在BeanDefinition中查找的, 不会提前实例化/初始化bean对象、
     * 然而 getBeansXXX()系列的方法, 会触发bean的实例化/初始化, 导致bean对象初始化不完整, 从而引发问题。
     */
    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        /**
         *  想要动态修改注解属性、需要在实例化之后/初始化之前, 进行注解属性的修改、
         *  @see CustomInstantiationAwareBeanPostProcessor#postProcessAfterInstantiation(Object, String)
         */
        // 获取存在自定义注解的Bean、进行相关操作, 给注解的属性动态赋值！
        // TODO: 可以在此时动态处理@Cacheable等注解的值、

        /**
         * 错误做法
         */
        /*Map<String, Object> beans = beanFactory.getBeansWithAnnotation(Entity.class);
        System.out.println("beans = " + beans);
        beans.forEach((k, val) -> {
            Entity annotation = val.getClass().getAnnotation(Entity.class);
            InvocationHandler handler = Proxy.getInvocationHandler(annotation);
            Field memberValues;
            try {
                memberValues = handler.getClass().getDeclaredField("memberValues");
                memberValues.setAccessible(true);
                memberValues.setAccessible(true);
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) memberValues.get(handler);
                map.put("name", val.getClass().getSimpleName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });*/

        /**
         * 正确用法、 可以修改{@link BeanDefinition}
         */
        String[] beanNamesForAnnotation = beanFactory.getBeanNamesForAnnotation(Service.class);
        String[] names = beanFactory.getBeanDefinitionNames();
        for (String name : names) {
            BeanDefinition definition = beanFactory.getBeanDefinition(name);
            definition.setLazyInit(true);
        }
    }

    /**
     * 如果无法通过{@link BeanDefinition}修改的Bean的内容, 就需要通过这种方式完成对Bean的内容动态赋值、
     * 实例化之后, 动态修改Bean中注解的属性值、
     */
    @Component
    public class CustomInstantiationAwareBeanPostProcessor implements InstantiationAwareBeanPostProcessor {
        @Override
        public boolean postProcessAfterInstantiation(Object bean, String beanName) throws BeansException {
            if (bean.getClass().isAnnotationPresent(Entity.class)) {
                Entity annotation = bean.getClass().getAnnotation(Entity.class);
                InvocationHandler handler = Proxy.getInvocationHandler(annotation);
                Field memberValues;
                try {
                    memberValues = handler.getClass().getDeclaredField("memberValues");
                    memberValues.setAccessible(true);
                    memberValues.setAccessible(true);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> map = (Map<String, Object>) memberValues.get(handler);
                    map.put("name", bean.getClass().getSimpleName());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }
}
