package com.luxf.custom.controller;

import com.luxf.custom.aware.IApplicationEventPublisherAware;
import com.luxf.custom.aware.IAware;
import com.luxf.custom.aware.IBeanFactoryAware;
import com.luxf.custom.aware.IEnvironmentAware;
import com.luxf.custom.config.IProperties;
import com.luxf.custom.entity.BaseInfo;
import com.luxf.custom.entity.User;
import com.luxf.custom.event.CustomEvent;
import com.luxf.custom.event.CustomEventPublisher;
import com.luxf.custom.event.CustomListener;
import com.luxf.custom.event.IApplicationEvent;
import com.luxf.custom.helper.ApplicationContextHelper;
import com.luxf.custom.helper.ReflectUtils;
import com.luxf.custom.service.UserService;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.aop.aspectj.annotation.AnnotationAwareAspectJAutoProxyCreator;
import org.springframework.aop.framework.autoproxy.AbstractAutoProxyCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Map;

/**
 * @author 小66
 */
@RestController
public class UserController {

    private final UserService userService;

    @Autowired
    private SqlSessionTemplate sqlSessionTemplate;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Autowired
    private IProperties iProperties;

    @RequestMapping("user/{id}")
    public String selectUserById(@PathVariable int id) {
        // 使用ApplicationContext也可以发布事件、 ApplicationContext 继承了 ApplicationEventPublisher。
        ApplicationContext context = ApplicationContextHelper.getApplicationContext();
        ApplicationEventPublisher eventPublisher = IApplicationEventPublisherAware.getEventPublisher();
        // 发布 继承于ApplicationEvent 的自定义监听事件、
        IApplicationEvent event = new IApplicationEvent("12345");
        eventPublisher.publishEvent(event);

        /**
         * 完全自定义的简单编写事件的发布、监听操作。
         */
        CustomEventPublisher publisher = new CustomEventPublisher();
        CustomListener listener = new CustomListener();
        publisher.addListener(listener);
        CustomEvent customEvent = new CustomEvent("54321");
        publisher.publishEvent(customEvent);

        Environment environment = IEnvironmentAware.getEnvironment();
        String property = environment.getProperty("server.port");
        // 默认情况下是DefaultListableBeanFactory实现的BeanFactory接口、
        DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) IBeanFactoryAware.getBeanFactory();
        User bean = beanFactory.getBean(User.class);
        // 通过ImportBeanDefinitionRegistrar接口注入的Bean、
        BaseInfo baseInfo = beanFactory.getBean(BaseInfo.class.getName(), BaseInfo.class);
        // 通过ImportSelector接口注入的Bean、
        IAware aware = beanFactory.getBean(IAware.class);
        // userBean和iProperties是 通过BeanDefinitionRegistryPostProcessor动态注入到Spring容器中的Bean对象
        User userBean = ApplicationContextHelper.getBean(User.class);
        System.out.println("userBean = " + userBean);
        System.out.println("iProperties = " + iProperties);
        Entity annotation = iProperties.getClass().getAnnotation(Entity.class);
        System.out.println("annotation.name() = " + annotation.name());
        User user = userService.selectUserById(id);
        return user == null ? "NULL" : user.toString();
    }

    @GetMapping("/mybatis")
    public void getMybatisConfiguration() {
        Configuration configuration = sqlSessionTemplate.getConfiguration();
        /**
         * MapperRegistry：Mapper接口注册器。如果没有指定{@link MapperScan#annotationClass()}属性时, 该Mapper接口注册器会注册其他的Interface接口,引发错误！
         * TODO: 包含注解和service接口、
         */
        MapperRegistry mapperRegistry = configuration.getMapperRegistry();
        Collection<Class<?>> mappers = mapperRegistry.getMappers();
    }

    @GetMapping("/proxy-map")
    public Map<Object, Class<?>> getProxyMap() {
        // 获取 Spring 动态代理创建器对象、
        AnnotationAwareAspectJAutoProxyCreator proxyCreator = ApplicationContextHelper.getBean(AnnotationAwareAspectJAutoProxyCreator.class);
        /**
         * {@link AbstractAutoProxyCreator#proxyTypes} 该对象中,包含所有的已创建的动态代理对象的Map
         */
        Field proxyTypes = ReflectUtils.findField(AnnotationAwareAspectJAutoProxyCreator.class, "proxyTypes");
        Map<Object, Class<?>> proxyTypesMap = ReflectUtils.getFieldValue(proxyCreator, proxyTypes);

        Field advisedBeans = ReflectUtils.findField(AnnotationAwareAspectJAutoProxyCreator.class, "advisedBeans");
        // 获取所有进入过AbstractAutoProxyCreator.wrapIfNecessary()方法的Bean的Map、
        Map<Object, Boolean> advisedBeansMap= ReflectUtils.getFieldValue(proxyCreator, advisedBeans);


        System.out.println("proxyTypesMap.size() = " + proxyTypesMap.size());
        return proxyTypesMap;
    }
}
