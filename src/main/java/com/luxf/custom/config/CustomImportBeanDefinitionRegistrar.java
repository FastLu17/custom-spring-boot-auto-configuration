package com.luxf.custom.config;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.annotation.MapperScannerRegistrar;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collection;
import java.util.Set;

/**
 * 通过自定义{@link Import}注解的参数的值{ImportSelector、ImportBeanDefinitionRegistrar、Configuration},
 * 完成所需要操作、(不一定会利用{@link BeanDefinitionRegistry}注入Bean对象)。
 * 例如 {@link MapperScannerRegistrar},就只是扫描相应的Mapper接口,并未向容器中注入MapperScannerRegistrar这个对象。
 * <p>
 * TODO: processImports()会处理 ImportSelector、ImportBeanDefinitionRegistrar、Configuration。
 * {@link ConfigurationClassParser#processImports(ConfigurationClass, ConfigurationClassParser.SourceClass, Collection, boolean)}
 *
 * TODO: 何时会调用{@link ImportBeanDefinitionRegistrar}接口的实现方法？
 * 大致的调用链：
 * {@link ConfigurationClassPostProcessor#processConfigBeanDefinitions(BeanDefinitionRegistry)}该方法内通过{@link ConfigurationClassBeanDefinitionReader#loadBeanDefinitions(Set)}
 * 最终走到{@link ConfigurationClassBeanDefinitionReader#loadBeanDefinitionsForConfigurationClass(ConfigurationClass, ConfigurationClassBeanDefinitionReader.TrackedConditionEvaluator)}
 *
 * // 阅读特定的{@link ConfigurationClass}，为类本身及其所有{@link Bean}方法注册bean定义。
 * private void loadBeanDefinitionsForConfigurationClass(
 * 			ConfigurationClass configClass, TrackedConditionEvaluator trackedConditionEvaluator) {
 *
 * 		if (trackedConditionEvaluator.shouldSkip(configClass)) {
 * 			String beanName = configClass.getBeanName();
 * 			if (StringUtils.hasLength(beanName) && this.registry.containsBeanDefinition(beanName)) {
 * 				this.registry.removeBeanDefinition(beanName);
 *          }
 * 			this.importRegistry.removeImportingClass(configClass.getMetadata().getClassName());
 * 			return;
 * 		}
 *
 * 		if (configClass.isImported()) {
 * 	        // 将{@link Configuration}类本身注册为Bean定义。
 * 			registerBeanDefinitionForImportedConfigurationClass(configClass);
 * 		}
 * 		for (BeanMethod beanMethod : configClass.getBeanMethods()) {
 * 	        // 加载带有{@link Bean}注解的方法，根据其内容向{@link BeanDefinitionRegistry}注册Bean定义。
 * 			loadBeanDefinitionsForBeanMethod(beanMethod);
 * 		}
 *
 * 		loadBeanDefinitionsFromImportedResources(configClass.getImportedResources());
 *
 * 	    // 加载所有通过实现{@link ImportBeanDefinitionRegistrar#registerBeanDefinitions(AnnotationMetadata, BeanDefinitionRegistry)}接口注入的BeanDefinitions。
 * 		loadBeanDefinitionsFromRegistrars(configClass.getImportBeanDefinitionRegistrars());
 * }
 *
 * @author 小66
 * @date 2020-08-17 15:41
 **/
public class CustomImportBeanDefinitionRegistrar implements ImportBeanDefinitionRegistrar {
    /**
     * 想要此方法生效,需要将值赋值给{@link Import#value}、
     *
     * @param importingClassMetadata
     * @param registry
     */
//    @Override
//    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
//        BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(BaseInfo.class)
//                .addPropertyValue("id", UUID.randomUUID().toString());
//        AbstractBeanDefinition definition = builder.getBeanDefinition();
//        String className = definition.getBeanClassName();
//        registry.registerBeanDefinition(className, definition);
//    }


    /**
     * 使用自定义注解{@link ImportRegistrar}来注入Bean,类似{@link MapperScan}
     * 想要此方法生效, 需要将{@link CustomImportBeanDefinitionRegistrar}的Class对象赋值给{@link Import#value}、
     */
    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(importingClassMetadata.getAnnotationAttributes(ImportRegistrar.class.getName()));
        if (annoAttrs == null) {
            return;
        }
        Class<?>[] classes = annoAttrs.getClassArray("value");
        for (Class<?> clazz : classes) {
            BeanDefinitionBuilder builder = BeanDefinitionBuilder.rootBeanDefinition(clazz);
            AbstractBeanDefinition definition = builder.getBeanDefinition();
            String className = definition.getBeanClassName();
            registry.registerBeanDefinition(className, definition);
        }
    }
}
