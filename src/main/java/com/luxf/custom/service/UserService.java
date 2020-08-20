package com.luxf.custom.service;

import com.luxf.custom.entity.User;

/**
 * 正常情况每个Service都需要实现AbstractDaoImpl、
 *
 * @author 小66
 * @date 2020-08-20 10:25
 **/
public interface UserService {

    User selectUserById(int id);
}
