package com.harvey.w.boot.init;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.web.WebApplicationInitializer;

import com.harvey.w.core.context.Context;

public class ServletContextInitializer implements WebApplicationInitializer {

    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        Context.setServletContext(servletContext);
    }

}
