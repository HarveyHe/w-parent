package com.harvey.w.dubbo.consumer;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.harvey.w.dubbo.listener.ReferenceConfigConsumeListener;

public class ReferenceFactoryBean implements FactoryBean<Object>, ApplicationContextAware, InitializingBean, DisposableBean {

    private List<ConsumerConfig> consumerConfigs;

    private List<ReferenceConfigConsumeListener> listeners;

    private ApplicationConfig applicationConfig;

    private List<RegistryConfig> registries;

    private Class<?> serviceType;
    private ReferenceConfig<?> referenceConfig;
    private ApplicationContext applicationContext;

    public ReferenceFactoryBean(Class<?> serviceType) {
        this.serviceType = serviceType;
    }

    @Override
    public Object getObject() throws Exception {
        if (referenceConfig == null) {
            doConsume();
        }
        return referenceConfig.get();
    }

    @Override
    public Class<?> getObjectType() {
        return serviceType;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }

    private void doConsume() {
        referenceConfig = new ReferenceConfig<>();
        referenceConfig.setCheck(false);
        referenceConfig.setInterface(serviceType);
        ConsumerWrapper.wrap(applicationConfig, consumerConfigs, registries).apply(referenceConfig);
        if (listeners != null) {
            for (ReferenceConfigConsumeListener listener : listeners) {
                if (listener.isSupport(serviceType, referenceConfig)) {
                    listener.onConsume(serviceType, referenceConfig);
                }
            }
        }
    }

    public void setConsumerConfigs(List<ConsumerConfig> consumerConfigs) {
        this.consumerConfigs = consumerConfigs;
    }

    @Override
    public void destroy() throws Exception {
        ConsumerWrapper.unwrap();
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        consumerConfigs = new ArrayList<>(applicationContext.getBeansOfType(ConsumerConfig.class).values());
        listeners = new ArrayList<>(applicationContext.getBeansOfType(ReferenceConfigConsumeListener.class).values());
        registries = new ArrayList<>(applicationContext.getBeansOfType(RegistryConfig.class).values());

        try {
            applicationConfig = applicationContext.getBean(ApplicationConfig.class);
        } catch (Exception ex) {
        }
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
