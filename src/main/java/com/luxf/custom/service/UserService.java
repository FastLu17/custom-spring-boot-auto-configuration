package com.luxf.custom.service;

import com.luxf.custom.entity.User;
import com.luxf.custom.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 正常情况每个Service都需要实现AbstractDaoImpl、
 *
 * @author 小66
 */
@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    @Cacheable(key = "#id", value = "USER")
    public User selectUserById(int id) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("id", id);
        map.put("tableName", "user");
        return userMapper.selectUserById(map, id);
    }
}
