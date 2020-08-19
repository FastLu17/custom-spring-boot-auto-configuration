package com.luxf.custom.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

/**
 * 想要直接使用Spring提供的事件发布监听机制, 则Event必须继承{@link ApplicationEvent}、Listener必须实现{@link ApplicationListener}
 * TODO: 自定义的Listener必须添加到容器中、
 *
 * @author 小66
 * @date 2020-08-18 13:01
 **/
public class IApplicationEvent extends ApplicationEvent {

    private final Object param;

    public IApplicationEvent(Object source) {
        super(source);
        param = source;
    }

    public Object getParam() {
        return param;
    }
}
