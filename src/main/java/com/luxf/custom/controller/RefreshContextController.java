package com.luxf.custom.controller;

import com.luxf.custom.helper.ApplicationContextHelper;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.AbstractRefreshableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @author 小66
 * @date 2020-08-31 9:46
 **/
@RestController
public class RefreshContextController {

    @GetMapping("/refresh-context")
    public Boolean refreshApplicationContext() {
        ApplicationContext context = ApplicationContextHelper.getApplicationContext();
        System.out.println("contextName = " + context.getClass().getSimpleName());
        if (context instanceof AbstractApplicationContext) {
            /**
             * {@link AbstractApplicationContext}存在2个直接子类：{@link GenericApplicationContext}和{@link AbstractRefreshableApplicationContext}
             *
             * 由于在Web应用中,{@link ApplicationContext}接口使用的实现类是{@link AnnotationConfigServletWebServerApplicationContext},
             * 该类继承于{@link GenericApplicationContext},在执行refresh()方法时,会调用{@link GenericApplicationContext#refreshBeanFactory()}
             * 该方法会校验是否已被刷新,重复刷新就会抛出异常！
             *
             * TODO：如果想要支持重复刷新,请选择AbstractApplicationContext的另外一个直接子类{@link AbstractRefreshableApplicationContext}
             */
            AbstractApplicationContext abstractApplicationContext = (AbstractApplicationContext) context;
            abstractApplicationContext.refresh();
        }
        return true;
    }
}
