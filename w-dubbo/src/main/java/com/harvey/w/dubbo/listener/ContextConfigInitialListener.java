package com.harvey.w.dubbo.listener;

import java.util.ArrayList;
import java.util.List;

import com.harvey.w.dubbo.constant.Constant;
import com.harvey.w.dubbo.consumer.ReferenceFactoryBean;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.common.utils.ConfigUtils;
import com.harvey.w.core.config.Config;
import com.harvey.w.core.config.listener.ContextConfigListener;
import com.harvey.w.core.utils.ResourceUtils;
import com.harvey.w.dubbo.provider.ServiceProviderBeanPostProcessor;
import com.harvey.w.dubbo.service.DubboService;

/**
 * 初始化dubbo 默认暴露的base service是@sse DubboService
 * 也可以在properties配置文件里配置为暴露为其他base service properties配置文件可选项 #指定暴露的baseService
 * w.dubbo.baseService=com.harvey.w.core.service.BaseService
 * 是否暴露服务(服务提供方需要设置为true, 消费方可以将此值设为false) w.dubbo.exposeService=true
 * 是否建立消费方服务(服务提供方可以将此值设为false, 消费方需要设置为true),需要在配置文件里设置sys.basePackage
 * w.dubbo.consumeService=true
 */
public class ContextConfigInitialListener implements ContextConfigListener,Ordered {

    @Override
    public void beforeStartup(final ConfigurableApplicationContext context) {
        context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                // w dubbo 配置
                ConfigUtils.setProperties(Config.getProperties());
                ContextConfigInitialListener.this.exposeService(context, registry);
                ContextConfigInitialListener.this.consumeService(context, registry);
            }
        });

    }

    @Override
    public void afterStartup(ConfigurableApplicationContext context) {
        // TODO Auto-generated method stub

    }

    /**
     * 建立消费方服务
     * 
     * @param context
     */
    private void consumeService(ConfigurableApplicationContext context, BeanDefinitionRegistry registry) {
        if (Boolean.FALSE.toString().equalsIgnoreCase(Config.get(Constant.DubboConsumeService))) {
            return;
        }

        List<Class<?>> consumeServices = getConsumeServiceInterface();
        for (Class<?> serviceType : consumeServices) {
            String beanName = StringUtils.uncapitalize(serviceType.getSimpleName());
            if (!registry.isBeanNameInUse(beanName)) {
                BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ReferenceFactoryBean.class).addConstructorArgValue(serviceType).getBeanDefinition();
                registry.registerBeanDefinition(beanName, beanDefinition);
            }
        }
    }

    private List<Class<?>> getConsumeServiceInterface() {
        List<Class<?>> sources = ResourceUtils.getClasses(Config.get("sys.basePackage"), "*.class");
        Class<?> baseClass = getBaseClass();
        List<Class<?>> result = new ArrayList<Class<?>>();
        for (Class<?> clazz : sources) {
            if (clazz.isInterface() && baseClass.isAssignableFrom(clazz)) {
                result.add(clazz);
            }
        }
        return result;
    }

    /**
     * 暴露提供方服务
     * 
     * @param context
     */
    private void exposeService(ApplicationContext context, BeanDefinitionRegistry registry) {
        if (Boolean.FALSE.toString().equalsIgnoreCase(Config.get(Constant.DubboExposeService))) {
            return;
        }
        Class<?> baseClass = getBaseClass();
        BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(ServiceProviderBeanPostProcessor.class).addConstructorArgValue(baseClass).getBeanDefinition();
        registry.registerBeanDefinition(ServiceProviderBeanPostProcessor.class.getName(), beanDefinition);
    }

    private Class<?> getBaseClass() {
        String clazzStr = Config.get(Constant.DubboBaseService);
        if (!StringUtils.isEmpty(clazzStr)) {
            try {
                return Class.forName(clazzStr);
            } catch (Exception ex) {

            }
        }
        return DubboService.class;
    }

    @Override
    public int getOrder() {
        return Integer.MAX_VALUE;
    }
}
