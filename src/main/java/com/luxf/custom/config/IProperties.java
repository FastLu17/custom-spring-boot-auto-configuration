package com.luxf.custom.config;

import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;

import javax.persistence.Entity;

/**
 * 该对象用于通过{@link BeanDefinitionRegistryPostProcessor}来动态注入Spring容器、
 *
 * @author 小66
 * @date 2020-08-15 21:01
 **/
@Entity(name = "NAME")
public class IProperties {

    private String port;

    public IProperties() {
    }

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "IProperties{" +
                "port='" + port + '\'' +
                '}';
    }

    // 测试Class.forName()和ClassLoader.loadClass()的区别。
    static {
        System.out.println("IProperties static block init！");
    }
}
