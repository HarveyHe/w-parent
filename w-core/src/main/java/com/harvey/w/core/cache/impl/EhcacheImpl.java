package com.harvey.w.core.cache.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.sf.ehcache.Ehcache;
import net.sf.ehcache.Element;
import net.sf.ehcache.search.Attribute;
import net.sf.ehcache.search.Query;
import net.sf.ehcache.search.Result;

import org.springframework.core.io.Resource;

import com.harvey.w.core.cache.AbstractCache;
import com.harvey.w.core.cache.utils.EhcacheUtils;
import com.harvey.w.core.model.PagingInfo;
import com.harvey.w.core.utils.BeanUtils;

public class EhcacheImpl extends AbstractCache {

    private Ehcache cache;
    private Resource cacheConfig;

    @Override
    protected Collection<?> onQuery(Object bean, Class<?> itemClass, String orderBy, PagingInfo pagingInfo) {
        Query query = this.cache.createQuery();
        Map<String, Object> parameter = bean == null ? Collections.<String, Object> emptyMap() : BeanUtils.toMap(bean);
        for (Entry<String, Object> entry : parameter.entrySet()) {
            Attribute<Object> attr = this.cache.getSearchAttribute(entry.getKey());
            if (attr == null) {
                attr = new Attribute<Object>(entry.getKey());
            }
            query.addCriteria(attr.eq(entry.getValue()));
        }
        List<Result> results = null;
        if (pagingInfo != null) {
            query.maxResults(pagingInfo.getPageSize());
            pagingInfo.setTotalRows(this.cache.getSize());
            results = query.execute().range(pagingInfo.getCurrentRow(), pagingInfo.getPageSize());
        } else {
            results = query.execute().all();
        }
        List<Object> items = new ArrayList<Object>();
        for(Result result : results){
            items.add(result.getValue());
        }
        return items;
    }

    @Override
    protected Object onGet(Object key) {
        Element el = this.cache.get(key);
        if (el != null) {
            return el.getObjectValue();
        }
        return null;
    }

    @Override
    protected Collection<?> onGets(Object... keys) {
        Map<?, ?> results = this.cache.getAll(Arrays.asList(keys));
        return results.values();
    }
    
    @Override
    protected void onReload() {
        this.clear();
    }

    @Override
    public void put(Object key, Object value) {
        Element el = new Element(key, value);
        this.cache.put(el);
    }

    @Override
    public void puts(Map<?, ?> elements) {
        for (Entry<?, ?> entry : elements.entrySet()) {
            this.put(entry.getKey(), entry.getValue());
        }
    }

    @Override
    public void evict(Object key) {
        this.cache.remove(key);
    }

    @Override
    public void evicts(Object... keys) {
        this.cache.removeAll(Arrays.asList(keys));
    }

    public Ehcache getCache() {
        return cache;
    }

    public void setCache(Ehcache cache) {
        this.cache = cache;
    }

    public void setCacheConfig(Resource cacheConfig) {
        this.cacheConfig = cacheConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.cache == null) {
            this.cache = EhcacheUtils.getEhCache(getName(), null, cacheConfig);
        }
        super.afterPropertiesSet();
    }

	@Override
	public Ehcache getNativeCache() {
		return this.cache;
	}

	@Override
	public void clear() {
		this.cache.removeAll();
	}

}
