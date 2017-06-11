package com.harvey.w.boot.init;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import com.harvey.w.core.config.Config;
import com.harvey.w.core.config.ConfigLoader;
import com.harvey.w.core.spring.BeanNameGenerator;
import com.harvey.w.core.spring.ContextConfigInitializer;

public class ApplicationRunListener implements SpringApplicationRunListener {

    private SpringApplication application;
    
    public ApplicationRunListener(SpringApplication application, String[] args) {
        this.application = application;
        application.setBeanNameGenerator(new BeanNameGenerator());
    }

    @Override
    public void started() {

    }

    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        //ConfigurableEnvironment env = context.getEnvironment();
        //environment.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", Config.getProperties()));
        //environment.setActiveProfiles("defaultProperties");
    }

    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        ContextConfigInitializer.configContext(Config.get("sys.configLocations"), context);
        ConfigurableEnvironment env = context.getEnvironment();
        env.getPropertySources().addLast(new PropertiesPropertySource("defaultProperties", Config.getProperties()));
        env.setActiveProfiles("defaultProperties");  
        /*ConfigLoader loader = ConfigLoader.getConfigLoader();
        if(loader.getAppContextList().size() > 0) {
            this.application.getSources().addAll(loader.getAppContextList());
        }*/
    }

    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {

    }

    @Override
    public void finished(ConfigurableApplicationContext context, Throwable exception) {
        ContextConfigInitializer.fireContextConfigAfterStartupListener(ConfigLoader.getConfigLoader(), context);
    }

}
