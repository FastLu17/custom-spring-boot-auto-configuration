package com.luxf.custom;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.aop.AopAutoConfiguration;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.jmx.JmxAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskExecutionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.thymeleaf.ThymeleafAutoConfiguration;
import org.springframework.boot.autoconfigure.web.client.RestTemplateAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.*;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.nio.file.Paths;

/**
 * Spring Boot中的手动Import所需的AutoConfiguration,可以大幅度提高启动性能。取消{@link EnableAutoConfiguration}
 * 因为{@link SpringBootApplication}内部具体实现就是通过{@link Import}实现、
 * <p>
 * {@link SpringBootServletInitializer}：继承该类,可以将SpringBoot项目打war包。与手动Import无关、
 * {@link WebServerFactoryCustomizer}：实现该接口,可以自定义Web服务器的内容。与手动Import无关、
 *
 * @author 小66
 */
// 也可使用@SpringBootConfiguration、该注解内部就是 @Configuration
@Configuration
@EnableTransactionManagement
@Import({ServletWebServerFactoryAutoConfiguration.class, CacheAutoConfiguration.class,
        DispatcherServletAutoConfiguration.class, WebMvcAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class, HttpEncodingAutoConfiguration.class,
        HttpMessageConvertersAutoConfiguration.class, JacksonAutoConfiguration.class,
        JmxAutoConfiguration.class, MultipartAutoConfiguration.class,
        PropertyPlaceholderAutoConfiguration.class, ThymeleafAutoConfiguration.class,
        RedisAutoConfiguration.class, DataSourceAutoConfiguration.class,
        RestTemplateAutoConfiguration.class, TaskSchedulingAutoConfiguration.class,
        TaskExecutionAutoConfiguration.class, MailSenderAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class, AopAutoConfiguration.class,
        MybatisAutoConfiguration.class})
@ComponentScan(
        basePackages = {"com.luxf"}
)
@MapperScan({"com.luxf"})
public class CustomApplication extends SpringBootServletInitializer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    public static void main(String[] args) {
        SpringApplication.run(CustomApplication.class, args);
    }

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        ((TomcatServletWebServerFactory) factory).addContextCustomizers((context) -> {
            File file = new File(Paths.get(System.getProperty("user.dir")) + "/tomcat");
            boolean mkdirs = file.mkdirs();
            if (mkdirs) {
                context.setDocBase(file.getAbsolutePath());
                context.setSessionTimeout(50000);
            }
        });
    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(CustomApplication.class);
    }
}
