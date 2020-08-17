package com.luxf.custom.config;

import org.springframework.boot.autoconfigure.AutoConfigurationImportSelector;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ImportSelector;
import org.springframework.context.annotation.*;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Collection;
import java.util.stream.Stream;

/**
 * TODO: processImports()会处理 ImportSelector、ImportBeanDefinitionRegistrar、Configuration。
 * {@link ConfigurationClassParser#processImports(ConfigurationClass, ConfigurationClassParser.SourceClass, Collection, boolean)}
 * // 源码具体理解
 * private void processImports(ConfigurationClass configClass, SourceClass currentSourceClass,
 * 			Collection<SourceClass> importCandidates, boolean checkForCircularImports) {
 *
 * 		if (importCandidates.isEmpty()) {
 * 			return;
 *      }
 *
 * 		if (checkForCircularImports && isChainedImportOnStack(configClass)) {
 * 			this.problemReporter.error(new CircularImportProblem(configClass, this.importStack));
 *      } else {
 * 			this.importStack.push(configClass);
 * 			try {
 * 				for (SourceClass candidate : importCandidates) {
 * 					if (candidate.isAssignable(ImportSelector.class)) {
 * 						// 候选人是{@link ImportSelector}
 * 						Class<?> candidateClass = candidate.loadClass();
 * 						ImportSelector selector = BeanUtils.instantiateClass(candidateClass, ImportSelector.class);
 * 						ParserStrategyUtils.invokeAwareMethods( selector, this.environment, this.resourceLoader, this.registry);
 *
 * 					    // {@link DeferredImportSelector}只有1个直接子类{@link AutoConfigurationImportSelector},
 * 					    // 用于SpringBoot的{@link EnableAutoConfiguration}
 * 					    // 判断是否是延迟的ImportSelector、
 * 						if (selector instanceof DeferredImportSelector) {
 * 							this.deferredImportSelectorHandler.handle(configClass, (DeferredImportSelector) selector);
 *                        } else {
 *                          // 普通的、自定义的{@link ImportSelector}
 * 							String[] importClassNames = selector.selectImports(currentSourceClass.getMetadata());
 * 							Collection<SourceClass> importSourceClasses = asSourceClasses(importClassNames);
 *                          // 递归调用{@link ConfigurationClassParser#processImports(ConfigurationClass, ConfigurationClassParser.SourceClass, Collection, boolean)}
 * 							processImports(configClass, currentSourceClass, importSourceClasses, false);
 *                        }
 *                    } else if (candidate.isAssignable(ImportBeanDefinitionRegistrar.class)) {
 * 						// 候选人是{@link ImportBeanDefinitionRegistrar}
 * 						Class<?> candidateClass = candidate.loadClass();
 * 						ImportBeanDefinitionRegistrar registrar =
 * 								BeanUtils.instantiateClass(candidateClass, ImportBeanDefinitionRegistrar.class);
 * 						ParserStrategyUtils.invokeAwareMethods(
 * 								registrar, this.environment, this.resourceLoader, this.registry);
 * 						configClass.addImportBeanDefinitionRegistrar(registrar, currentSourceClass.getMetadata());
 *                    } else {
 * 						// Candidate class not an ImportSelector or ImportBeanDefinitionRegistrar ->
 * 						// 都不是的时候,当做{@link Configuration}来处理、
 * 						this.importStack.registerImport(
 * 								currentSourceClass.getMetadata(), candidate.getMetadata().getClassName());
 * 						processConfigurationClass(candidate.asConfigClass(configClass));
 *                    }
 *                }
 *            } catch (BeanDefinitionStoreException ex) {
 * 				throw ex;
 *            } catch (Throwable ex) {
 * 				throw new BeanDefinitionStoreException(
 * 						"Failed to process import candidates for configuration class [" +
 * 						configClass.getMetadata().getClassName() + "]", ex);
 *            } finally {
 * 				this.importStack.pop();
 *            }
 *        }
 * }
 *
 * @author 小66
 * @date 2020-08-17 19:26
 **/
public class CustomImportSelector implements ImportSelector {

    /**
     * 使用自定义注解{@link com.luxf.custom.config.ImportSelector}来注入Bean、
     * 想要此方法生效, 需要将{@link CustomImportSelector}的Class对象赋值给{@link Import#value}、
     */
    @Override
    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
        AnnotationAttributes annoAttrs = AnnotationAttributes.fromMap(
                importingClassMetadata.getAnnotationAttributes(com.luxf.custom.config.ImportSelector.class.getName()));
        if (annoAttrs == null) {
            return new String[0];
        }
        Class<?>[] classes = annoAttrs.getClassArray("value");
        return Stream.of(classes).map(Class::getName).toArray(String[]::new);
    }
}
