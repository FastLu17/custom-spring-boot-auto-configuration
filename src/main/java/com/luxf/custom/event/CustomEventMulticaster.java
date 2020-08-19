package com.luxf.custom.event;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 事件多播器
 *
 * @author 小66
 * @date 2020-08-18 10:27
 * @see org.springframework.context.event.SimpleApplicationEventMulticaster
 **/
class CustomEventMulticaster {
    final Map<Class<?>, List<CustomListener>> customListenerMap = new ConcurrentHashMap<>();

    void multicastEvent(final CustomEvent event) {
        customListenerMap.forEach((key, listeners) -> {
            // 条件根据实际来设定即可、主要是判断当前event和listener需要对应。根据当前事件类型过滤、
            // 这个条件目前是不成立的、onCustomEvent()方法的参数可以对应Event、
//            if (key.isAssignableFrom(event.getSource().getClass())) {
//                listeners.forEach(listener -> invokeListener(listener, event));
//            }
            listeners.forEach(listener -> invokeListener(listener, event));
        });
    }

    private void invokeListener(CustomListener listener, CustomEvent event) {
        listener.onCustomEvent(event);
    }
}
