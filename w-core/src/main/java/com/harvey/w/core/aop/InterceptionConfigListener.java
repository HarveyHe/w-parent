package com.harvey.w.core.aop;

import org.springframework.aop.config.AopConfigUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;

import com.harvey.w.core.config.Config;
import com.harvey.w.core.config.listener.ContextConfigListener;

public class InterceptionConfigListener implements ContextConfigListener,BeanFactoryPostProcessor {

	@Override
	public void beforeStartup(ConfigurableApplicationContext context) {
		//是否启动Aop注解拦截
		if(!"false".equals(Config.get("sys.enableInterception"))){
			context.addBeanFactoryPostProcessor(this);	
		}
	}

	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
		AopConfigUtils.registerAutoProxyCreatorIfNecessary(registry);
		registryProxy(registry);
	}

	private void registryProxy(BeanDefinitionRegistry registry){
		
		final String descriptorSourceBeanName = "eaf.aop.interceptionSource";
		final String interceptionInvokerBeanName= "eaf.aop.interceptionInvoker";
		final String interceptionPointcutAdvisorBeanName="eaf.aop.interceptionPointcutAdvisor";
		
		BeanDefinitionBuilder sourceBuilder = BeanDefinitionBuilder.rootBeanDefinition(InterceptionDescriptorSource.class);
		sourceBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		
		BeanDefinitionBuilder interceptorBuilder = BeanDefinitionBuilder.rootBeanDefinition(InterceptionInvoker.class);
		interceptorBuilder.addConstructorArgReference(descriptorSourceBeanName);
		interceptorBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		
		BeanDefinitionBuilder advisorBuilder = BeanDefinitionBuilder.rootBeanDefinition(ProxyPointcutAdvisor.class);
		advisorBuilder.addPropertyValue("adviceBeanName", interceptionInvokerBeanName);
		advisorBuilder.addPropertyReference("descriptorSource", descriptorSourceBeanName);
		advisorBuilder.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
		
		registry.registerBeanDefinition(descriptorSourceBeanName, sourceBuilder.getBeanDefinition());
		registry.registerBeanDefinition(interceptionInvokerBeanName,interceptorBuilder.getBeanDefinition());
		registry.registerBeanDefinition(interceptionPointcutAdvisorBeanName,advisorBuilder.getBeanDefinition());
	}

	@Override
	public void afterStartup(ConfigurableApplicationContext context) {
		
	}
}
