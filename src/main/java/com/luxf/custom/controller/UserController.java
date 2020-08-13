package com.luxf.custom.controller;

import com.luxf.custom.entity.User;
import com.luxf.custom.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    @RequestMapping("user/{id}")
    public String selectUserById(@PathVariable int id) {
        User user = userService.selectUserById(id);
        return user == null ? "NULL" : user.toString();
    }

}
