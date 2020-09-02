package other.pack.path.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfiguration;

/**
 * 将此文件中的Bean对象初始化到独立容器中.
 * TODO：如果需要Spring的定时任务功能, 需要添加{@link EnableScheduling}注解, 或者通过{@link Import}引入对应的{@link SchedulingConfiguration}
 *
 * @author 小66
 * @date 2020-09-02 9:38
 **/
@Configuration
public class RefreshableConfiguration {

    @Bean
    public Test test() {
        return new Test();
    }

    @Bean
    public RefreshBeanFactoryPostProcessor refreshBeanFactoryPostProcessor() {
        return new RefreshBeanFactoryPostProcessor();
    }
}
