package com.luxf.custom;

import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
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
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.nio.file.Paths;

/**
 * Spring Boot中的手动Import所需的AutoConfiguration,可以大幅度提高启动性能。取消{@link EnableAutoConfiguration}
 * 因为{@link SpringBootApplication}内部具体实现就是通过{@link Import}实现、
 * <p>
 * {@link SpringBootServletInitializer}：继承该类,可以将SpringBoot项目打war包(pom.xml中要排除Spring-boot自带的Tomcat)。与手动Import无关、
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
@ComponentScan(basePackages = {"com.luxf"})
@MapperScan({"com.luxf"})
public class CustomApplication extends SpringBootServletInitializer implements WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    /**
     * new SpringApplicationBuilder(CustomApplication.class)和SpringApplication.run(CustomApplication.class, args);
     * 都是执行{@link org.springframework.boot.SpringApplication#SpringApplication(ResourceLoader, Class[])}构造方法
     * <p>
     * 可以利用SpringApplicationBuilder初始化一些特定的内容、
     * <p>
     * 自定义banner：
     * <p>
     *      1.文本Banner：可以通过在classpath路径下添加banner.txt文件或者设置spring.banner.location属性来指定自定义Banner文件；如果文件格式不是UTF-8，可以设置spring.banner.charset属性
     * <p>
     *      2.图片Banner:在classpath路径下添加banner.gif、banner.jpg、banner.png图片或者通过spring.banner.image.location属性来指定文件；效果如下所示：
     * <p>
     *      3.编程方式：SpringApplication.setBanner(…)使用编程的方式设置banner，使用org.springframework.boot.Banner接口实现printBanner()方法；
     *
     *  TODO: {@link SpringApplication#refreshContext(ConfigurableApplicationContext)} 是核心方法、
     *  方法内部调用{@link AbstractApplicationContext#refresh()}：详细如下
     *  TODO: 该方法内进行容器初始化Bean的相关操作、
     *  public void refresh() throws BeansException, IllegalStateException {
     *         synchronized(this.startupShutdownMonitor) {
     *             this.prepareRefresh();
     *             ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
     *             this.prepareBeanFactory(beanFactory);
     *
     *             try {
     *                 this.postProcessBeanFactory(beanFactory);
     *                 this.invokeBeanFactoryPostProcessors(beanFactory);
     *                 this.registerBeanPostProcessors(beanFactory);
     *                 this.initMessageSource();
     *                 this.initApplicationEventMulticaster();
     *                 TODO: 调用子类的onRefresh()、典型的就是{@link ServletWebServerApplicationContext#onRefresh()}、
     *                      该方法内部调用createWebServer();创建内置的Tomcat、
     *                 this.onRefresh();
     *                 this.registerListeners();
     *                 this.finishBeanFactoryInitialization(beanFactory);
     *                 this.finishRefresh();
     *             } catch (BeansException var9) {
     *                 this.destroyBeans();
     *                 this.cancelRefresh(var9);
     *                 throw var9;
     *             } finally {
     *                 this.resetCommonCaches();
     *             }
     *
     *         }
     *     }
     */
    public static void main(String[] args) {
        // 启动方式1：
//        CustomSpringApplication application = new CustomSpringApplication(CustomApplication.class);
//        application.setRegisterShutdownHook(true);
//        application.run(args);

        // 启动方式2：
        SpringApplicationBuilder builder = new SpringApplicationBuilder(CustomApplication.class)
                // 关闭Banner、
                .bannerMode(Banner.Mode.OFF)
                // WebApplicationType.deduceFromClasspath()会推断类型、此时指定WEB容器类型会覆盖推断的结果、
                .web(WebApplicationType.SERVLET)
                // 默认是true、打印启动日志
                // 配置spring.main.log-startup-info=false,同样可以关闭启动日志、
                .logStartupInfo(false)
                // 设置是否创建的{@link ApplicationContext}应该注册一个关闭钩子。缺省值为{@code true},以确保可以正常处理JVM关闭。
                // 如果设置为false,则devtools自动重启将不起作用。
                .registerShutdownHook(false);
        builder.build().run(args);
        // 启动方式3：
//         SpringApplication.run(CustomApplication.class, args);
    }

    /**
     * 自定义内置的Tomcat相关内容、
     * @param factory
     */
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
        // 设置是否创建的{@link ApplicationContext}应该注册一个关闭钩子。缺省值为{@code true},以确保可以正常处理JVM关闭。
        builder.registerShutdownHook(true);
        return builder.sources(CustomApplication.class);
    }

    /**
     * {@link SpringApplication#afterRefresh(ConfigurableApplicationContext, ApplicationArguments)}是空实现,可以根据需求,自定义实现相关操作
     */
    private static class CustomSpringApplication extends SpringApplication {
        private CustomSpringApplication(Class<?>... primarySources) {
            super(primarySources);
        }
        @Override
        protected void afterRefresh(ConfigurableApplicationContext context, ApplicationArguments args) {
            // 父类的afterRefresh()是空实现、可以自定义相关操作、
            super.afterRefresh(context, args);
            System.out.println("context.getApplicationName() = " + context.getApplicationName());
        }
    }
}
