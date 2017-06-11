package com.harvey.w.dubbo.listener;

import com.alibaba.dubbo.config.ServiceConfig;

/**
 * 在暴露service时触发此事件,可以在事件里对暴露的方法进行定制
 * 
 * @author harvey
 * 
 */
public interface ServiceConfigExposeListener {
    boolean isSupport(Object service, ServiceConfig<?> serviceConfig);

    void onExpose(Object service, ServiceConfig<?> serviceConfig);
}
