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
import com.luxf.custom.service.UserService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;
import java.lang.reflect.Field;

/**
 * @author 小66
 */
@RestController
public class UserController {

    private final UserService userService;

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
        BaseInfo baseInfo = beanFactory.getBean(BaseInfo.class.getName(),BaseInfo.class);
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

}
