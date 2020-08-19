package com.luxf.custom.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.support.AbstractApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

/**
 * 事件发布器、如果需要可以定义为Spring Bean对象。
 * {@link AbstractApplicationContext} 实现{@link ApplicationEventPublisher}接口
 *
 * @author 小66
 * @date 2020-08-18 10:19
 * @see ApplicationEventPublisher
 * @see AbstractApplicationContext
 **/
public class CustomEventPublisher {

    private final CustomEventMulticaster eventMulticaster;

    public CustomEventPublisher() {
        this.eventMulticaster = new CustomEventMulticaster();
    }

    public void addListener(CustomListener... listeners) {
        Stream.of(listeners).forEach(listener -> {
            List<CustomListener> customListeners = eventMulticaster.customListenerMap.remove(listener.getClass());
            customListeners = customListeners == null ? new ArrayList<>() : customListeners;
            customListeners.add(listener);
            eventMulticaster.customListenerMap.put(listener.getClass(), customListeners);
        });
    }

    public void publishEvent(CustomEvent event) {
        eventMulticaster.multicastEvent(event);
    }
}
