package com.harvey.w.core.spring;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.ConfigurableWebApplicationContext;

public class ContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        if (applicationContext instanceof ConfigurableWebApplicationContext) {
            ServletContext servletContext = ((ConfigurableWebApplicationContext) applicationContext).getServletContext();
            ContextConfigInitializer.configContext(servletContext, ((ConfigurableWebApplicationContext) applicationContext));
        } else {
            ContextConfigInitializer.configContext(null, applicationContext);
        }
    }

}
