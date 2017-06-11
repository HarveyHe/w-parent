package com.harvey.w.core.spring;

import java.lang.reflect.Method;

import javax.servlet.ServletContext;

import com.harvey.w.core.config.ConfigLoader;
import com.harvey.w.core.config.listener.ContextConfigListener;
import com.harvey.w.core.config.listener.ServletContextConfigListener;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.xml.ResourceEntityResolver;
import org.springframework.beans.factory.xml.XmlBeanDefinitionReader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;

import com.harvey.w.core.config.Config;
import com.harvey.w.core.context.AbstractContext;

public class ContextConfigInitializer {

    private static void doLoadXmlLocationsConfig(final String[] configLocations, final ConfigurableApplicationContext context) {
        if (configLocations == null || configLocations.length == 0)
            return;
        context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

            @Override
            public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
                XmlBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader(registry);
                beanDefinitionReader.setEnvironment(context.getEnvironment());
                beanDefinitionReader.setResourceLoader(context);
                beanDefinitionReader.setEntityResolver(new ResourceEntityResolver(context));
                for (String configLocation : configLocations) {
                    beanDefinitionReader.loadBeanDefinitions(configLocation);
                }
            }
        });
    }

    public static void configContext(ConfigurableWebApplicationContext context) {
        configContext(context.getServletContext(), context);
    }

    public static void configContext(ServletContext servletContext, ConfigurableWebApplicationContext context) {
        ConfigLoader configLoader = ConfigLoader.loadConfig(servletContext);
        AbstractContext.setContext(context);
        if (configLoader.getAppContextList().size() > 0) {
            String[] configLocations = configLoader.getAppContextList().toArray(new String[configLoader.getAppContextList().size()]);
            try {
                context.setConfigLocations(configLocations);
            } catch (Exception ex) {
                doLoadXmlLocationsConfig(configLocations, context);
            }
        }
        if (servletContext != null) {
            servletContext.setAttribute("config", Config.getProperties());
        }
        // context.getEnvironment().getPropertySources().addLast(new
        // PropertiesPropertySource("defaultProperties",
        // Config.getProperties()));
        // context.getEnvironment().setActiveProfiles("defaultProperties");
        fireContextConfigBeforeStartupListener(configLoader, context);
    }

    public static void configContext(String configLocations, ConfigurableApplicationContext context) {
        ConfigLoader configLoader = ConfigLoader.loadConfig(configLocations);
        AbstractContext.setContext(context);
        if (configLoader.getAppContextList().size() > 0) {
            String[] configs = configLoader.getAppContextList().toArray(new String[configLoader.getAppContextList().size()]);
            try {
                Method method = context.getClass().getMethod("setConfigLocations", configs.getClass());
                method.invoke(context, new Object[] { configs });
            } catch (Throwable throwable) {
                doLoadXmlLocationsConfig(configs, context);
            }
        }
        // ConfigurableEnvironment env = context.getEnvironment();
        // env.getPropertySources().addLast(new
        // PropertiesPropertySource("defaultProperties",
        // Config.getProperties()));
        // env.setActiveProfiles("defaultProperties");
        /*
         * if(context instanceof AbstractXmlApplicationContext &&
         * !context.getBeanFactory
         * ().containsBean(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR
         * )) {
         * context.getBeanFactory().registerSingleton(AnnotationConfigUtils.
         * CONFIGURATION_BEAN_NAME_GENERATOR, new BeanNameGenerator() ); }
         */
        fireContextConfigBeforeStartupListener(configLoader, context);
    }

    public static ConfigurableApplicationContext createApplicationContext() {
        return createApplicationContext(Config.get("sys.configLocations"));
    }

    public static ConfigurableApplicationContext createApplicationContext(String configLocations) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext();
        configContext(configLocations, context);
        context.refresh();
        fireContextConfigAfterStartupListener(ConfigLoader.getConfigLoader(), context);
        return context;
    }

    public static void configDispactherServletContext(SpringDispatcherServlet servlet, ConfigurableApplicationContext context) {
        fireServletContextConfigBeforeStartupListener(ConfigLoader.loadConfig(servlet.getServletContext()), context, servlet);
    }

    public static void fireContextConfigBeforeStartupListener(ConfigLoader configLoader, ConfigurableApplicationContext context) {
        for (ContextConfigListener listener : configLoader.getContextConfigListeners()) {
            listener.beforeStartup(context);
        }
    }

    public static void fireServletContextConfigBeforeStartupListener(ConfigLoader configLoader, ConfigurableApplicationContext context, SpringDispatcherServlet servlet) {
        for (ServletContextConfigListener listener : configLoader.getServletContextConfigListeners()) {
            listener.beforeStartup(context, servlet);
        }
    }

    public static void fireContextConfigAfterStartupListener(ConfigLoader configLoader, ConfigurableApplicationContext context) {
        for (ContextConfigListener listener : configLoader.getContextConfigListeners()) {
            listener.afterStartup(context);
        }
    }

    public static void fireServletContextConfigAfterStartupListener(ConfigLoader configLoader, ConfigurableApplicationContext context, SpringDispatcherServlet servlet) {
        for (ServletContextConfigListener listener : configLoader.getServletContextConfigListeners()) {
            listener.afterStartup(context, servlet);
        }
    }
}
