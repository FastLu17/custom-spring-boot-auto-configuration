package com.luxf.custom.event;

import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * 如果自定义的ApplicationListener没有添加到Spring容器中, 监听无法生效。
 *
 * @author 小66
 * @date 2020-08-18 13:04
 **/
@Component
public class IApplicationListener implements ApplicationListener<IApplicationEvent> {

    @Override
    public void onApplicationEvent(IApplicationEvent event) {
        Object param = event.getParam();
        System.out.println("param = " + param);
    }
}
