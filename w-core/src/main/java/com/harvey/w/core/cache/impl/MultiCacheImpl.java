package com.harvey.w.core.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.harvey.w.core.cache.AbstractCache;
import com.harvey.w.core.cache.Cache;
import org.springframework.util.Assert;

import com.harvey.w.core.model.PagingInfo;

/**
 * 多级缓存实现 L1 L2 L3.... 多级缓存的caches不应该有数据提供者(dataProvider)
 * 
 * @author admin
 * 
 */
public class MultiCacheImpl extends AbstractCache {
	private List<Cache> caches = new ArrayList<Cache>();

	@Override
	public void put(Object key, Object value) {
		for (Cache cache : caches) {
			cache.put(key, value);
		}
	}

	@Override
	public void puts(Map<?, ?> elements) {
		for (Cache cache : caches) {
			cache.puts(elements);
		}
	}

	@Override
	public void evict(Object key) {
		for (Cache cache : caches) {
			cache.evict(key);
		}
	}

	@Override
	public void evicts(Object... keys) {
		for (Cache cache : caches) {
			cache.evicts(keys);
		}
	}

	@Override
	protected void onReload() {
		for (Cache cache : caches) {
			cache.reload();
		}
	}

	@Override
	protected Collection<?> onQuery(Object parameter, Class<?> itemClass, String orderBy, PagingInfo pagingInfo) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object onGet(Object key) {
		for (Cache cache : caches) {
			Object val = cache.get(key);
			if (val != null) {
				return val;
			}
		}
		return null;
	}

	@Override
	protected Collection<?> onGets(Object... keys) {
		for (Cache cache : caches) {
			Collection<?> vals = cache.gets(keys);
			if (vals != null && !vals.isEmpty()) {
				return vals;
			}
		}
		return Collections.emptyList();
	}

	public void setCaches(List<Cache> caches) {
		if (caches != null) {
			this.caches.addAll(caches);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.isTrue(!this.caches.isEmpty(), "No any more cache for multi caches");
		super.afterPropertiesSet();
	}

	@Override
	public List<Cache> getNativeCache() {
		return this.caches;
	}

	@Override
	public void clear() {
		for (Cache cache : caches) {
			cache.clear();
		}
	}

}
