package com.luxf.custom.aware;

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.event.EventPublicationInterceptor;
import org.springframework.stereotype.Component;

/**
 * 通过自定义事件(需要继承{@link ApplicationEvent})完成所对应的监听功能、
 *
 * @author 小66
 * @date 2020-08-18 11:41
 * @see EventPublicationInterceptor ApplicationEventPublisherAware的典型实现类之一、
 **/
@Component
public class IApplicationEventPublisherAware implements ApplicationEventPublisherAware {

    private static ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        IApplicationEventPublisherAware.eventPublisher = applicationEventPublisher;
    }

    /**
     * 自定义Event、
     *
     * @param event
     */
    public void pulishEvent(ApplicationEvent event) {
        eventPublisher.publishEvent(event);
    }

    public static ApplicationEventPublisher getEventPublisher() {
        return eventPublisher;
    }
}
