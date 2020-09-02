package com.luxf.custom.factory;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 利用{@link AnnotationConfigWebApplicationContext}容器可重复刷新的特性.
 *
 * @author 小66
 * @date 2020-09-02 9:27
 **/
@Component
public class RefreshableContextFactory implements DisposableBean, ApplicationContextAware {
    private final Map<String, AnnotationConfigWebApplicationContext> contexts = new ConcurrentHashMap<>();
    private ApplicationContext parent;

    @Override
    public void destroy() {
        Collection<AnnotationConfigWebApplicationContext> values = this.contexts.values();
        for (AnnotationConfigWebApplicationContext context : values) {
            context.close();
        }
        this.contexts.clear();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.parent = applicationContext;
    }

    public AnnotationConfigWebApplicationContext refreshContext(Class<?> annotatedClasses) {
        return refreshContext(annotatedClasses.getName());
    }

    private AnnotationConfigWebApplicationContext refreshContext(String name) {
        if (!this.contexts.containsKey(name)) {
            throw new RuntimeException("Standalone Container [" + name + "] not exist!");
        }
        synchronized (this.contexts) {
            contexts.get(name).refresh();
        }
        return this.contexts.get(name);
    }

    public void refreshAllContext() {
        if (this.contexts.size() > 0) {
            synchronized (this.contexts) {
                this.contexts.forEach((key, val) -> val.refresh());
            }
        }
    }

    public AnnotationConfigWebApplicationContext getContext(Class<?> annotatedClasses) {
        String name = annotatedClasses.getName();
        if (!this.contexts.containsKey(name)) {
            synchronized (this.contexts) {
                if (!this.contexts.containsKey(name)) {
                    this.contexts.put(name, createContext(name, annotatedClasses));
                }
            }
        }
        return this.contexts.get(name);
    }

    private AnnotationConfigWebApplicationContext createContext(String name, Class<?> annotatedClasses) {
        /**
         * {@link AnnotationConfigWebApplicationContext}是可以进行重复刷新的.
         * {@link AnnotationConfigApplicationContext}不可以进行重复刷新.
         */
        AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
        context.register(annotatedClasses);
        // 同样可以使用scan()方法进行扫描、
        // context.scan("other.pack.path.config");
        if (this.parent != null) {
            context.setParent(this.parent);
            context.setClassLoader(this.parent.getClassLoader());
        }
        context.setDisplayName(name);
        context.refresh();
        return context;
    }
}
