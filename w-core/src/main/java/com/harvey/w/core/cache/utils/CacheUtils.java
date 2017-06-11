package com.harvey.w.core.cache.utils;

import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.core.io.Resource;
import org.springframework.util.ClassUtils;

public abstract class CacheUtils {

	private static final boolean EhCachePresent = ClassUtils.isPresent("net.sf.ehcache.CacheManager", ClassUtils.getDefaultClassLoader());
	
	public static Cache getCache(String cacheName,Resource config) {
		if(EhCachePresent) {
			return EhcacheUtils.getCache(cacheName, null, config);
		}
		return new ConcurrentMapCache(cacheName);
	}
	
	public static Cache getCache(String cacheName,Map<String,Object> config) {
		if(EhCachePresent) {
			return EhcacheUtils.getCache(cacheName, config, null);
		}
		return new ConcurrentMapCache(cacheName);
	}
	
    public static MultiKey genMultiKey(Object... keys) {
        MultiKey key = new MultiKey(keys);
        return key;
    }
}
