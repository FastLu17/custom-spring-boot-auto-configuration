package com.luxf.custom.aware;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * 可以这样获取{@link Environment}接口的Bean对象、与 @Autowired 获取的 Environment的Bean相同、
 *
 * @author 小66
 * @date 2020-08-16 22:35
 **/
@Component
public class IEnvironmentAware implements EnvironmentAware {
    private static Environment environment;

    @Override
    public void setEnvironment(Environment environment) {
        IEnvironmentAware.environment = environment;
    }

    public static Environment getEnvironment() {
        return environment;
    }
}
