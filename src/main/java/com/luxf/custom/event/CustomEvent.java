package com.luxf.custom.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;

import java.util.EventObject;
import java.util.Map;

/**
 * 自定义事件、
 * <p>
 * TODO: 想要直接使用Spring提供的事件发布监听机制, 则Event必须继承{@link ApplicationEvent}、Listener必须实现{@link ApplicationListener}
 *
 * @author 小66
 * @date 2020-08-18 10:09
 * @see ApplicationEvent
 **/
public class CustomEvent extends EventObject {
    /**
     * 事件携带的参数、
     */
    private final Map<String, Object> eventParam;

    /**
     * System time when the event happened.
     */
    private final long timestamp;


    /**
     * Constructs a prototypical Event.
     *
     * @param source The object on which the Event initially occurred.
     * @throws IllegalArgumentException if source is null.
     */
    public <T> CustomEvent(T source, Map<String, Object> eventParam) {
        super(source);
        this.timestamp = System.currentTimeMillis();
        this.eventParam = eventParam;
    }

    public <T> CustomEvent(T source) {
        this(source, null);
    }

    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }

    public final Map<String, Object> getEventParam() {
        return eventParam;
    }

    public final <T> T getOriginalSource() {
        @SuppressWarnings("unchecked")
        T source = (T) super.getSource();
        return source;
    }
}
