package com.harvey.w.core.config.listener;

import org.springframework.context.ConfigurableApplicationContext;

import com.harvey.w.core.spring.SpringDispatcherServlet;

public interface ServletContextConfigListener {
	void beforeStartup(ConfigurableApplicationContext servletApplicationContext,SpringDispatcherServlet servlet);
	void afterStartup(ConfigurableApplicationContext servletApplicationContext,SpringDispatcherServlet servlet);
}
