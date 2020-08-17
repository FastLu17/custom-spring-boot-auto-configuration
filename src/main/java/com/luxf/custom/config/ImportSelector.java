package com.luxf.custom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

import java.lang.annotation.*;

/**
 * 自定义有关于{@link org.springframework.context.annotation.ImportSelector}接口的注解
 *
 * @author 小66
 * @date 2020-08-17 16:41
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CustomImportSelector.class)
public @interface ImportSelector {
    /**
     * 需要被注入Spring容器,成为Spring Bean的对象。
     * 可以是{@link org.springframework.context.annotation.ImportSelector},
     * {@link ImportBeanDefinitionRegistrar},{@link Configuration},需要成为Spring Bean的普通对象、
     */
    Class<?>[] value();
}
