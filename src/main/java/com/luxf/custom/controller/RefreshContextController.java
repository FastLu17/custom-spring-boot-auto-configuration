package com.luxf.custom.controller;

import com.luxf.custom.factory.RefreshableContextFactory;
import com.luxf.custom.helper.ApplicationContextHelper;
import com.luxf.custom.helper.ReflectUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import other.pack.path.config.RefreshableConfiguration;
import other.pack.path.config.Test;

import javax.annotation.Resource;
import javax.persistence.Entity;
import java.util.Map;


/**
 * @author 小66
 * @date 2020-08-31 9:46
 **/
@RestController
public class RefreshContextController {
    @Resource
    private RefreshableContextFactory contextFactory;

    /**
     * 初始化独立容器.
     * 正常情况是在主容器初始化完成后,就执行独立容器的初始化.按需处理即可！
     */
    @GetMapping("/create")
    public Boolean createContext() {
        AnnotationConfigWebApplicationContext context = contextFactory.getContext(RefreshableConfiguration.class);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Map<String, BeanDefinition> beanDefinitionMap = ReflectUtils.getFieldValue(beanFactory, "beanDefinitionMap");
        Test test = context.getBean(Test.class);
        Entity entity = test.getClass().getAnnotation(Entity.class);
        String name = entity.name();
        System.out.println("name = " + name);
        /**
         * TODO：未做特殊处理时,主容器{@link ApplicationContext}中不存在独立创建的容器{@link AnnotationConfigWebApplicationContext}中的Bean.
         *
         * {@link Test}是由独立容器进行初始化的,因此通过主容器获取该Bean对象,会由于找不到而抛出异常.
         */
        // Test fromParentContainer = ApplicationContextHelper.getBean(Test.class);
        // System.out.println("fromParentContainer = " + fromParentContainer);
        return true;
    }

    /**
     * 刷新容器、
     */
    @GetMapping("/refresh")
    public Boolean refreshContext() {
        // 刷新所有的独立容器.
        // contextFactory.refreshAllContext();
        // 刷新指定的独立容器
        AnnotationConfigWebApplicationContext context = contextFactory.refreshContext(RefreshableConfiguration.class);
        ConfigurableListableBeanFactory beanFactory = context.getBeanFactory();
        Map<String, BeanDefinition> beanDefinitionMap = ReflectUtils.getFieldValue(beanFactory, "beanDefinitionMap");
        Test test = context.getBean(Test.class);
        Entity entity = test.getClass().getAnnotation(Entity.class);
        String name = entity.name();
        System.out.println("name = " + name);
        return true;
    }

    @GetMapping("/refresh-context")
    public Boolean refreshApplicationContext() {
        ApplicationContext context = ApplicationContextHelper.getApplicationContext();
        System.out.println("contextName = " + context.getClass().getSimpleName());
        if (context instanceof AbstractApplicationContext) {
            /**
             * {@link AbstractApplicationContext}存在2个直接子类：{@link GenericApplicationContext}和{@link AbstractRefreshableApplicationContext}
             *
             * 由于在Web应用中,{@link ApplicationContext}接口使用的实现类是{@link AnnotationConfigServletWebServerApplicationContext},
             * 该类继承于{@link GenericApplicationContext},在执行refresh()方法时,会调用{@link GenericApplicationContext#refreshBeanFactory()}
             * 该方法会校验是否已被刷新,重复刷新就会抛出异常！
             *
             * TODO：如果想要支持重复刷新,请选择AbstractApplicationContext的另外一个直接子类{@link AbstractRefreshableApplicationContext}
             */
            AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) context;
            abstractApplicationContext.refresh();
        }
        return true;
    }
}
