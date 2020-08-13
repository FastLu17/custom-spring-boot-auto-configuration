package com.luxf.custom.mapper;

import com.luxf.custom.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @author luxf
 * @date 2020/2/24 22:53
 */
@Repository
@Mapper
public interface UserMapper {

    User selectUserById(Map<String, Object> map, Integer id);

}
