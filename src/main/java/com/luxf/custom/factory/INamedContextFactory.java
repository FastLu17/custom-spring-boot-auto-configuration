package com.luxf.custom.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import other.pack.path.config.OtherConfiguration;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * {@link AnnotationConfigApplicationContext}：该对象的注释、TODO: Standalone application context.独立的应用程序上下文、
 * 利用{@link AnnotationConfigApplicationContext}主动加载Spring容器未扫描到的@Configuration/@Bean等对象到单独的容器中.
 * <p>
 * 模仿{org.springframework.cloud.context.named.NamedContextFactory}
 *
 * @author 小66
 * @date 2020-08-24 18:00
 **/
@Component
public class INamedContextFactory implements DisposableBean, ApplicationContextAware {

    /**
     * 使用List的方式,可以获取容器中所有的{@link BeanDefinitionRegistryPostProcessor}对象(包含子类)、
     */
    @Resource
    private List<BeanDefinitionRegistryPostProcessor> registryPostProcessorList = Collections.emptyList();

    private final Map<String, AnnotationConfigApplicationContext> contexts = new ConcurrentHashMap<>();
    private ApplicationContext parent;

    @Override
    public void destroy() throws Exception {
        Collection<AnnotationConfigApplicationContext> values = this.contexts.values();
        for (AnnotationConfigApplicationContext context : values) {
            context.close();
        }
        this.contexts.clear();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parent = applicationContext;
    }

    public AnnotationConfigApplicationContext getContext(String name) {
        System.out.println("registryPostProcessorList.size() = " + registryPostProcessorList.size());
        if (!this.contexts.containsKey(name)) {
            synchronized (this.contexts) {
                if (!this.contexts.containsKey(name)) {
                    this.contexts.put(name, createContext(name));
                }
            }
        }
        return this.contexts.get(name);
    }

    private AnnotationConfigApplicationContext createContext(String name) {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        // 将没有扫描进来的Configuration对象,使用AnnotationConfigApplicationContext单独进行扫描、
        // 在NamedContextFactory中,对FeignClientsConfiguration和RibbonClientConfiguration分别进行了单独的扫描、
        // 针对不同的微服务、维护不同的AnnotationConfigApplicationContext处理。
        /**
         *
         * 在{@link NamedContextFactory}中,对{@link FeignClientsConfiguration}和{@link RibbonClientConfiguration}分别进行了单独的扫描、
         */
        context.register(OtherConfiguration.class);
        // 同样可以使用scan()方法进行扫描、
        context.scan("other.pack.path.config");

        if (this.parent != null) {
            context.setParent(this.parent);
            context.setClassLoader(this.parent.getClassLoader());
        }
        context.setDisplayName(name);
        context.refresh();
        return context;
    }
}
