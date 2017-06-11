package com.harvey.w.core.i18n.impl;

import java.util.Locale;

import com.harvey.w.core.cache.Cache;
import com.harvey.w.core.cache.utils.CacheUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.harvey.w.core.i18n.LocaleResolver;
import com.harvey.w.core.i18n.ResourceBundle;
import com.harvey.w.core.i18n.ResourceBundleManager;

public abstract class AbstractResourceBundleManager implements ResourceBundleManager,InitializingBean {

    private Cache cacheBean;
    private LocaleResolver localeResolver;
    
    public LocaleResolver getLocaleResolver() {
        return localeResolver;
    }

    public void setLocaleResolver(LocaleResolver localeResolver) {
        this.localeResolver = localeResolver;
    }


    @Override
    public void afterPropertiesSet() throws Exception {
        Assert.notNull(cacheBean, "ehcacheBean not allow null");
        Assert.notNull(localeResolver, "localeResolver not allow null");
    }    
    
    @Override
    public ResourceBundle getBundle(String bundleName) throws Exception {
        Locale locale = getLocaleResolver().resolveLocale();
        Object cacheKey = CacheUtils.genMultiKey(bundleName,locale);
        synchronized (cacheBean) {
            ResourceBundle bundle = cacheBean.getValue(cacheKey);
            if (bundle == null) {
                bundle = doGetBundle(bundleName, locale);
                if(bundle == null){
                    return bundle;
                }
                cacheBean.put(cacheKey,bundle);
            }
            return bundle;
        }
    }

    abstract ResourceBundle doGetBundle(String bundleName, Locale locale) throws Exception;

    public Cache getCacheBean() {
        return cacheBean;
    }

    public void setCacheBean(Cache cacheBean) {
        this.cacheBean = cacheBean;
    }
    
}
