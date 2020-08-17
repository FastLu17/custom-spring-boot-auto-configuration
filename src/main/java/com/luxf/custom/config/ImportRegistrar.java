package com.luxf.custom.config;

import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;

import java.lang.annotation.*;

/**
 * 自定义有关于{@link ImportBeanDefinitionRegistrar}接口的注解
 *
 * @author 小66
 * @date 2020-08-17 16:41
 **/
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Import(CustomImportBeanDefinitionRegistrar.class)
public @interface ImportRegistrar {
    /**
     * 需要被注入的Bean对象、
     *
     * @return
     */
    Class<?>[] value();
}
