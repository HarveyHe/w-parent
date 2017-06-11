package com.harvey.w.dubbo.listener;

import com.alibaba.dubbo.config.ReferenceConfig;

public interface ReferenceConfigConsumeListener {
    boolean isSupport(Class<?> serviceType, ReferenceConfig<?> referenceConfig);
    void onConsume(Class<?> serviceType, ReferenceConfig<?> referenceConfig);
}
