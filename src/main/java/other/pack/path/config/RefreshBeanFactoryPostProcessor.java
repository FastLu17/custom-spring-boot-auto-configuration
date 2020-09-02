package other.pack.path.config;

import com.luxf.custom.entity.User;
import com.luxf.custom.factory.RefreshableContextFactory;
import com.luxf.custom.helper.ApplicationContextHelper;
import com.luxf.custom.service.UserService;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import javax.persistence.Entity;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Map;

/**
 * @author 小66
 */
public class RefreshBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

    @Override
    @SuppressWarnings("unchecked")
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        // 获取存在自定义注解的Bean、进行相关操作, 给注解的属性动态赋值！
        Map<String, Object> beans = beanFactory.getBeansWithAnnotation(Entity.class);
        System.out.println("beans = " + beans);
        beans.forEach((k, val) -> {
            Entity annotation = val.getClass().getAnnotation(Entity.class);
            InvocationHandler handler = Proxy.getInvocationHandler(annotation);
            Field memberValues;
            try {
                memberValues = handler.getClass().getDeclaredField("memberValues");
                memberValues.setAccessible(true);

                Map<String, Object> map = (Map<String, Object>) memberValues.get(handler);
                /**
                 * 此处是通过主容器{@link org.springframework.context.ApplicationContext}获取的Bean对象. 可以正常获取！
                 * 不是通过{@link RefreshableContextFactory#contexts}维护的{@link AnnotationConfigWebApplicationContext}
                 */
                UserService userService = ApplicationContextHelper.getBean(UserService.class);
                // 同样可以利用主容器的进行数据库查询,完成重新给注解赋值.
                User user = userService.selectUserById(1);
                map.put("name", user.getUserName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
