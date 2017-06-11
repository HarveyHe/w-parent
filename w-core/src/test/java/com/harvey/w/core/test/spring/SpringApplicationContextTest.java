package com.harvey.w.core.test.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.StaticApplicationContext;

import com.harvey.w.core.spring.ContextConfigInitializer;
import com.harvey.w.core.test.bean.TestBean;
import com.harvey.w.core.test.bean.TestBeanA;
import com.harvey.w.core.test.bean.TestBeanBFactory;

public class SpringApplicationContextTest {

	public static void main(String[] args) {
		// testSaticApplicationContext();
		testClassPathApplicationContext();
	}

	private static void testClassPathApplicationContext() {
		ConfigurableApplicationContext context = ContextConfigInitializer.createApplicationContext("classpath:/applicationContext-test.xml");
		context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

			@Override
			public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

				BeanDefinition beanDefinition = BeanDefinitionBuilder.genericBeanDefinition(TestBeanBFactory.class).setLazyInit(false)
						.getBeanDefinition();
				BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
				registry.registerBeanDefinition("testBeanBByCode", beanDefinition);
				String[] names = beanFactory.getBeanDefinitionNames();
				for (String name : names) {
					if (name.equals("beanBFactoryByXml")) {
						BeanDefinition bd = beanFactory.getBeanDefinition(name);
						System.out.println(bd);
					}
				}
			}
		});
		context.refresh();
		TestBeanA bean = context.getBean(TestBeanA.class);
		System.out.println(bean.getSay());
		System.out.println(bean.getBeanB());
		context.stop();
	}

	private static void testSaticApplicationContext() {
		final ConfigurableApplicationContext context = createStaticApplicationContext();
		TestBean bean = new TestBean();
		context.getBeanFactory().registerSingleton("testBean", bean);
		context.registerShutdownHook();
		context.refresh();
		context.start();
		System.out.println("context started");
	}

	private static ConfigurableApplicationContext createStaticApplicationContext() {
		return new StaticApplicationContext();
	}

}
