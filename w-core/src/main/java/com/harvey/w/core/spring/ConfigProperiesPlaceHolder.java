package com.harvey.w.core.spring;


import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

import com.harvey.w.core.config.Config;

public class ConfigProperiesPlaceHolder extends PropertyPlaceholderConfigurer {

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        processProperties(beanFactory, Config.getProperties());
    }

}
