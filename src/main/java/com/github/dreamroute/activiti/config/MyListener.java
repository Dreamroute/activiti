package com.github.dreamroute.activiti.config;

import org.activiti.engine.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEventListener;

public class MyListener implements ActivitiEventListener {
    @Override
    public void onEvent(ActivitiEvent event) {
        System.err.println(event);
    }

    @Override
    public boolean isFailOnException() {
        return false;
    }
}
