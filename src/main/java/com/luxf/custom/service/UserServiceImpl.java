package com.luxf.custom.service;

import com.luxf.custom.entity.User;
import com.luxf.custom.mapper.UserMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    @Cacheable(key = "#id", value = "USER")
    @Transactional
    public User selectUserById(int id) {
        boolean aopProxy = AopUtils.isAopProxy(this);
        // AopContext.currentProxy()可以得到当前UserServiceImpl的代理对象, 可以通过该对象解决内部调用事务失效的问题。
        Object proxy = AopContext.currentProxy();
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        map.put("tableName", "user");
        return userMapper.selectUserById(map, id);
    }
}
