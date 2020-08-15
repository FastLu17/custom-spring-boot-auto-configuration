package com.luxf.custom.mapper;

import com.luxf.custom.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * 使用{@link MapperScan}后,可以不需要再使用{@link Mapper,Repository}
 *
 * @author luxf
 * @date 2020/2/24 22:53
 */
public interface UserMapper {

    User selectUserById(Map<String, Object> map, Integer id);

}
