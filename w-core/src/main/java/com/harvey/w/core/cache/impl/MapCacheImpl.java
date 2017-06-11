package com.harvey.w.core.cache.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.harvey.w.core.cache.AbstractCache;
import com.harvey.w.core.model.PagingInfo;

public class MapCacheImpl extends AbstractCache {

    private Map<Object,Object> cache = new ConcurrentHashMap<Object, Object>();
    
    @Override
    public void put(Object key, Object value) {
        cache.put(key, value);
    }

    @Override
    public void puts(Map<?, ?> elements) {
        cache.putAll(elements);
    }

    @Override
    public void evict(Object key) {
        cache.remove(key);
    }

    @Override
    public void evicts(Object... keys) {
        for(Object key : keys){
            this.evict(key);
        }
    }

    @Override
    protected void onReload() {
        this.clear();
    }

    @Override
    protected Collection<?> onQuery(Object parameter, Class<?> itemClass, String orderBy, PagingInfo pagingInfo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Object onGet(Object key) {
        return this.cache.get(key);
    }

    @Override
    protected Collection<?> onGets(Object... keys) {
        List<Object> values  = new ArrayList<Object>();
        for(Object key : keys){
            Object value = this.get(key);
            if(value != null){
                values.add(value);
            }
        }
        return values;
    }

	@Override
	public Map<Object,Object> getNativeCache() {
		return this.cache;
	}

	@Override
	public void clear() {
		this.cache.clear();
	}

}
