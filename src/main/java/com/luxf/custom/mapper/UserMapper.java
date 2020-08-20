package com.luxf.custom.mapper;

import com.luxf.custom.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.binding.MapperRegistry;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 使用{@link MapperScan}后,可以不需要再使用{@link Mapper,Repository} ---> 之前由于{@link Service}层没有继承Interface接口,没有发现错误！
 * <p>
 * {@link MapperScan}时,如果没有指定{@link MapperScan#annotationClass()}属性, 会扫描指定package下的所有的Interface去注册到{@link MapperRegistry#knownMappers}属性中、
 * <p>
 *
 * 在使用Service接口时,就会报错{@code org.apache.ibatis.binding.BindingException}
 * @author luxf
 * @date 2020/2/24 22:53
 */
@Mapper
public interface UserMapper {

    User selectUserById(Map<String, Object> map, Integer id);

}
