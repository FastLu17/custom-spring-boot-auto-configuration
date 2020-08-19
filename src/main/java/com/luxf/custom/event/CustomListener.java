package com.luxf.custom.event;

import java.util.EventListener;

/**
 * 事件监听器
 *
 * @author 小66
 * @date 2020-08-18 10:15
 * @see org.springframework.context.ApplicationListener
 **/
public class CustomListener implements EventListener {

    /**
     * Handle an CustomEvent event.
     *
     * @param event CustomEvent
     */
    void onCustomEvent(CustomEvent event) {
        String originalSource = event.getOriginalSource();
        System.out.println("onCustomEvent = " + originalSource);
    }
}
