package com.luxf.custom;

import com.luxf.custom.aware.IAware;
import com.luxf.custom.config.ImportRegistrar;
import com.luxf.custom.config.ImportSelector;
import com.luxf.custom.entity.BaseInfo;
import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
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
import org.springframework.boot.context.ConfigurationWarningsApplicationContextInitializer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.context.ServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.File;
import java.nio.file.Paths;
import java.util.Set;

/**
 * Spring Boot中的手动Import所需的AutoConfiguration,可以大幅度提高启动性能。取消{@link EnableAutoConfiguration}
 * 因为{@link SpringBootApplication}内部具体实现就是通过{@link Import}实现、
 *
 * {@link SpringBootApplication}内部的{@link EnableAutoConfiguration}就是通过{@link org.springframework.context.annotation.ImportSelector}接口
 * 和{@link ImportBeanDefinitionRegistrar}接口实现扫描需要自动配置(注入Spring容器)的类进行相关注册。
 * <p>
 * {@link SpringBootServletInitializer}：继承该类,可以将SpringBoot项目打war包(pom.xml中要排除Spring-boot自带的Tomcat)。与手动Import无关、
 * {@link WebServerFactoryCustomizer}：实现该接口,可以自定义Web服务器的内容。与手动Import无关、
 *
 * 添加包的扫描路径逻辑在{@link ConfigurationWarningsApplicationContextInitializer.ComponentScanPackageCheck#addComponentScanningPackages(Set, AnnotationMetadata)}
 * 基本逻辑就是：先判断是否存在{@link ComponentScan}注解,再判断该注解是否有相应属性值,再判断是否需要默认启动类的packageName、
 *
 * @see BeanDefinitionParser 用于解析各种xml文件中的{@link BeanDefinition}
 * @see ConfigurationClassParser 用于解析存在{@link Configuration}注解的Class.
 *
 * 在{@link ConfigurationClassParser#doProcessConfigurationClass(ConfigurationClass, ConfigurationClassParser.SourceClass)}方法中,
 * 会解析 {@link Import,ImportResource,ComponentScan,Bean,PropertySources}等注解、
 *
 * 自定义{@link ImportRegistrar}注解来注入指定的Bean对象、
 *
 * @see AopAutoConfiguration Aop的自动配置中, 包含了{@link EnableAspectJAutoProxy}注解, 因此不需要再使用{@link EnableAspectJAutoProxy}
 * 如果配置了多次{@link EnableAspectJAutoProxy},则在{@link AopConfigUtils#registerOrEscalateApcAsRequired(Class, BeanDefinitionRegistry, Object)}方法中校验容器中是否已存在该Bean,是否需要更新EnableAspectJAutoProxy相关的属性。
 *
 * @author 小66
 */
// 也可使用@SpringBootConfiguration、该注解内部就是 @Configuration
@Configuration
@EnableTransactionManagement(proxyTargetClass = true)
@EnableAspectJAutoProxy(exposeProxy = true)
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
//@MapperScan(value = {"com.luxf"})// 如果没有指定annotationClass属性,mybatis会在MapperRegistry中注册所有的Interface接口,引发错误。
@MapperScan(value = {"com.luxf"}, annotationClass = Mapper.class)
@ImportRegistrar({BaseInfo.class})
@ImportSelector({IAware.class})
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
     *  方法内部调用{@link AbstractApplicationContext#refresh()}：详细如下 --> 该方法就属于 Spring 的核心方法(Spring Boot基于Spring)、
     *  TODO: 该方法内进行容器初始化Bean的相关操作、
     *  public void refresh() throws BeansException, IllegalStateException {
     *         synchronized(this.startupShutdownMonitor) {
     *             // 准备此上下文用以进行刷新、
     *             this.prepareRefresh();
     *             // 让子类刷新内部的BeanFactory。
     *             ConfigurableListableBeanFactory beanFactory = this.obtainFreshBeanFactory();
     *             // 准备在此上下文中使用的BeanFactory。
     *             // 该方法内会初始化ApplicationContextAwareProcessor、
     *             @see org.springframework.context.support.ApplicationContextAwareProcessor
     *             this.prepareBeanFactory(beanFactory);
     *
     *             try {
     *                 // 允许在上下文子类中对BeanFactory进行后置处理。
     *                 this.postProcessBeanFactory(beanFactory);
     *                 // 调用在上下文中注册为BeanFactory的所有后置处理器。--> 来改变bean的定义,包括处理@Confiuration注解和@Import注解等、
     *                 // 可以实现{@link BeanDefinitionRegistryPostProcessor}接口向容器中动态注册Bean对象、
     *                 // {@link ConfigurationClassPostProcessor#postProcessBeanDefinitionRegistry(BeanDefinitionRegistry)}会解析@Confiuration注解和@Import注解等、
     *                 this.invokeBeanFactoryPostProcessors(beanFactory);
     *
     *                 // 注册Bean的后置处理器,在Bean创建过程中调用。
     *                 // 注册所有 BeanPostProcessors，这里会区分是否继承了 PriorityOrdered 和 Ordered
     *                 // 执行的优先级按照 PriorityOrdered > Ordered > 无
     *                 this.registerBeanPostProcessors(beanFactory);
     *
     *                 // 为此上下文初始化消息源。
     *                 this.initMessageSource();
     *                 // 初始化上下文中的事件机制。
     *                 this.initApplicationEventMulticaster();
     *                 TODO: 调用特殊子类的onRefresh()、典型的就是{@link ServletWebServerApplicationContext#onRefresh()}、
     *                      该方法内部调用createWebServer();创建内置的Tomcat、 (初始化其他的特殊Bean)
     *                 this.onRefresh();
     *                 // 检查侦听器bean并注册它们。
     *                 this.registerListeners();
     *                 // 实例化所有剩余的（non-lazy-init 非延迟初始化）单例Bean。
     *                 this.finishBeanFactoryInitialization(beanFactory);
     *                 // 发布对应的事件。
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
