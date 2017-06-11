package com.harvey.w.dubbo.provider;

import java.util.ArrayList;
import java.util.List;

import com.harvey.w.dubbo.listener.ServiceConfigExposeListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.alibaba.dubbo.common.bytecode.Proxy;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.harvey.w.core.utils.ReflectionUtils;

public class ServiceProviderBeanPostProcessor implements BeanPostProcessor,ApplicationContextAware, InitializingBean, DisposableBean {

    private static final String ProxyPackageName = Proxy.class.getPackage().getName();

    private List<ProviderConfig> providerConfigs;

    private List<ServiceConfigExposeListener> listeners;

    private ApplicationConfig applicationConfig;

    private List<RegistryConfig> registries;

    private List<ProtocolConfig> protocols;

    private Class<?> baseClass;
    private ApplicationContext applicationContext;

    public ServiceProviderBeanPostProcessor(Class<?> baseClass) {
        this.baseClass = baseClass;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Package pkg = bean.getClass().getPackage();
        if (pkg != null && !ProxyPackageName.equals(pkg.getName()) && baseClass.isInstance(bean)) {
            exposeService(bean);
        }
        return bean;
    }

    private void exposeService(Object service) {
        Class<?> intf = ReflectionUtils.getServiceInterface(service.getClass(), baseClass);
        ServiceConfig<Object> sc = new ServiceConfig<Object>();
        sc.setRef(service);
        sc.setInterface(intf);
        ProviderWrapper.wrap(applicationConfig, providerConfigs, registries, protocols).apply(sc);
        fireConfigExposeListener(service, sc);
        sc.export();
    }

    private void fireConfigExposeListener(Object service, ServiceConfig<?> serviceConfig) {
        if (listeners != null) {
            for (ServiceConfigExposeListener listener : this.listeners) {
                if (listener.isSupport(service, serviceConfig)) {
                    listener.onExpose(service, serviceConfig);
                }
            }
        }
    }

    public void setProviderConfigs(List<ProviderConfig> providerConfigs) {
        this.providerConfigs = providerConfigs;
    }

    @Override
    public void destroy() throws Exception {
        ProviderWrapper.unwrap();
    }

    public void setListeners(List<ServiceConfigExposeListener> listeners) {
        this.listeners = listeners;
    }

    public void setApplicationConfig(ApplicationConfig applicationConfig) {
        this.applicationConfig = applicationConfig;
    }

    public void setRegistries(List<RegistryConfig> registries) {
        this.registries = registries;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        providerConfigs = new ArrayList<>(applicationContext.getBeansOfType(ProviderConfig.class).values());
        listeners = new ArrayList<>(applicationContext.getBeansOfType(ServiceConfigExposeListener.class).values());
        registries = new ArrayList<>(applicationContext.getBeansOfType(RegistryConfig.class).values());
        protocols = new ArrayList<>(applicationContext.getBeansOfType(ProtocolConfig.class).values());
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
