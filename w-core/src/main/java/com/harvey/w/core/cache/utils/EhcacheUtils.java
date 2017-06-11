package com.harvey.w.core.cache.utils;

import java.util.Map;
import java.util.Map.Entry;

import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Ehcache;
import net.sf.ehcache.config.Configuration;
import net.sf.ehcache.config.DiskStoreConfiguration;

import org.springframework.cache.Cache;
import org.springframework.cache.ehcache.EhCacheCache;
import org.springframework.cache.ehcache.EhCacheFactoryBean;
import org.springframework.cache.ehcache.EhCacheManagerUtils;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

import com.harvey.w.core.bean.DefaultBeanWrapper;

public class EhcacheUtils {
	
	public static Cache getCache(String cacheName, Map<String, Object> props, Resource configResource) {
		Ehcache cache = getEhCache(cacheName,props,configResource);
		return new EhCacheCache(cache);
	}
	
	public static Ehcache getEhCache(String cacheName, Map<String, Object> props, Resource configResource) {
		Assert.hasText(cacheName, "cache name not be null");
		Ehcache cache = null;
		CacheManager cacheManager = CacheManager.getInstance();
		if (cacheManager.cacheExists(cacheName)) {
			cache = cacheManager.getEhcache(cacheName);
		}
		if (cache == null && configResource != null) {
			Configuration config = EhCacheManagerUtils.parseConfiguration(configResource);
			if (config.getName() == null) {
				config.setName(cacheName);
			}
			cacheManager = CacheManager.getCacheManager(config.getName());
			if (cacheManager == null) {
				cacheManager = CacheManager.newInstance(config);
			}
			cache = cacheManager.getEhcache(cacheName);
		}
		if (cache == null) {
			EhCacheFactoryBean factory = new EhCacheFactoryBean();
			if (props != null) {
				DefaultBeanWrapper wrapper = new DefaultBeanWrapper(factory);
				for (Entry<String, Object> entry : props.entrySet()) {
					try {
						wrapper.setPropertyValue(entry.getKey(), entry.getValue());
					} catch (Exception ex) {
					}
				}
			}
			factory.setName(cacheName);
			factory.afterPropertiesSet();
			cache = factory.getObject();
		}
		return cache;
	}

	public static String getDiskStorePath(Configuration configuration) {
		DiskStoreConfiguration diskStoreConfiguration = configuration.getDiskStoreConfiguration();
		if (diskStoreConfiguration == null) {
			return null;
		} else {
			return diskStoreConfiguration.getPath();
		}
	}

}
