package other.pack.path.config;

import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.persistence.Entity;

/**
 * 可以用于类似Spring定时任务注解value的动态更改,达到动态切换cron表达式的效果.
 * 注解值需要重复刷新,利用独立容器{@link AnnotationConfigWebApplicationContext}维护需要动态刷新的Bean对象.
 *
 * @author 小66
 * @date 2020-09-02 9:39
 **/
@Entity(name = "T_TEST")
public class Test {

    public void method() {
        System.out.println("true = " + true);
    }
}
