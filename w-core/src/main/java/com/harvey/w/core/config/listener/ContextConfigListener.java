package com.harvey.w.core.config.listener;

import org.springframework.context.ConfigurableApplicationContext;

public interface ContextConfigListener {
	void beforeStartup(ConfigurableApplicationContext context);
	void afterStartup(ConfigurableApplicationContext context); 
}
