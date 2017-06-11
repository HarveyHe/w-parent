package com.harvey.w.core.spring;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;

import com.harvey.w.core.config.ConfigLoader;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;
import org.springframework.web.context.ContextLoaderListener;

import com.harvey.w.core.context.Context;

public class SpringContextLoaderListener extends ContextLoaderListener {

    @Override
    protected void customizeContext(ServletContext servletContext, ConfigurableWebApplicationContext context) {
        try {
        	ContextConfigInitializer.configContext(servletContext, context);
            super.customizeContext(servletContext, context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void contextInitialized(ServletContextEvent event) {
        Context.setServletContext(event.getServletContext());
        //super.contextInitialized(event);
        ConfigLoader configLoader = ConfigLoader.loadConfig(event.getServletContext());
        ConfigurableApplicationContext applicationContext = (ConfigurableApplicationContext)super.initWebApplicationContext(event.getServletContext());
        ContextConfigInitializer.fireContextConfigAfterStartupListener(configLoader, applicationContext);
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        Context.setServletContext(null);
        Context.setContext(null);
        super.contextDestroyed(event);
    }

}
