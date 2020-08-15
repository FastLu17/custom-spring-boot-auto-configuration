package com.luxf.custom.controller;

import com.luxf.custom.config.IProperties;
import com.luxf.custom.entity.User;
import com.luxf.custom.helper.ApplicationContextHelper;
import com.luxf.custom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.persistence.Entity;

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
