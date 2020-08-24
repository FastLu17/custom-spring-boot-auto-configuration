package other.pack.path.config;

import com.luxf.custom.CustomApplication;
import com.luxf.custom.factory.INamedContextFactory;
import com.luxf.custom.helper.ReflectUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * {@link AnnotationConfigApplicationContext}：该对象的注释、TODO: Standalone application context.独立的应用程序上下文、
 * 利用{@link AnnotationConfigApplicationContext}主动加载Spring容器未扫描到的@Configuration/@Bean等对象到单独的容器中.
 * <p>
 * 该@Configuration对象不再{@link CustomApplication}的包路径之下,在没有单独配置@ComponentScan、@Import等情形下,不会注入到容器！
 *
 * @author 小66
 * @date 2020-08-24 17:56
 **/
@Configuration
public class OtherConfiguration {

    /**
     * 不会注入到 Spring 容器中, 经过{@link INamedContextFactory#getContext(String)}可以注入到新建独立的{@link AnnotationConfigApplicationContext}中
     */
    @Bean
    public ReflectUtils reflectHelper() {
        return new ReflectUtils();
    }
}
