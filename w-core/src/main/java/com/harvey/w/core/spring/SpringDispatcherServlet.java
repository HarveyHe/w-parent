package com.harvey.w.core.spring;

import com.harvey.w.core.config.ConfigLoader;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class SpringDispatcherServlet extends DispatcherServlet {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;
	private String configPattern;

	public String getConfigPattern() {
		return configPattern;
	}

	public void setConfigPattern(String configPattern) {
		this.configPattern = configPattern;
	}

	@Override
	protected WebApplicationContext createWebApplicationContext(WebApplicationContext parent) {
		String pattern = "**/**/servletContext-" + (StringUtils.isEmpty(configPattern) ? getServletName() : configPattern) + ".xml";
		ConfigLoader loader = ConfigLoader.loadConfig(getServletContext());
		String configLocation = StringUtils.join(loader.getServletContextList(pattern), ",");
		super.setContextConfigLocation(configLocation);
		WebApplicationContext context = super.createWebApplicationContext(parent);
		ContextConfigInitializer.configDispactherServletContext(this, (ConfigurableApplicationContext) context);
		return context;
	}

	@Override
	protected WebApplicationContext initWebApplicationContext() {
		WebApplicationContext applicationContext = super.initWebApplicationContext();
        ConfigLoader configLoader = ConfigLoader.loadConfig(getServletContext());
        ContextConfigInitializer.fireServletContextConfigAfterStartupListener(configLoader, (ConfigurableApplicationContext)applicationContext, this);
		return applicationContext;
	}

}
