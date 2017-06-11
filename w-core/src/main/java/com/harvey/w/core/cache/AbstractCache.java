package com.harvey.w.core.cache;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.support.SimpleValueWrapper;

import com.harvey.w.core.dao.utils.EntityUtils;
import com.harvey.w.core.model.PagingInfo;

public abstract class AbstractCache implements Cache, InitializingBean {

    protected boolean loaded = false;
    private DataProvider dataProvider;
    private String name;
    
    public String getName(){
        return this.name;
    }
    
    public void setName(String name){
        this.name = name;
    }

    @Override
    public void setDataProvider(DataProvider dataProvider) {
        this.dataProvider = dataProvider;
    }

    @Override
    public DataProvider getDataProvider() {
        return this.dataProvider;
    }

    @Override
    public void reload() {
        this.loaded = false;
        this.onReload();
        loadAllFromProvider(dataProvider.mode());
    }

    private void loadAllFromProvider(LoadMode mode) {
        if (!loaded && dataProvider != null && mode.equals(dataProvider.mode())) {
            Map<?, ?> data = dataProvider.getAllToCache();
            this.puts(data);
            loaded = true;
        }
    }

    private Object loadItemFromProvider(Object key) {
        if(isMode(LoadMode.ByNeed) || isMode(LoadMode.Lazy)){
            Object item = dataProvider.getItemToCache(key);
            this.put(key, item);
            return item;
        }
        return null;
    }

    private Collection<?> loadItemsFromProvider(Object... keys) {
        if(isMode(LoadMode.ByNeed) || isMode(LoadMode.Lazy)){
            Map<?,?> items = dataProvider.getItemsToCache(keys);
            this.puts(items);
            return items.values();
        }
        return new ArrayList<Object>();
    }

    private boolean isMode(LoadMode mode){
        return dataProvider != null && mode.equals(dataProvider.mode());
    }
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (isMode(LoadMode.OnInit)) {
            this.reload();
        }
    }

    @Override
    public final <T> Collection<T> queryCache(Object parameter, Class<T> itemClass, String orderBy, PagingInfo pagingInfo) {
        this.loadAllFromProvider(LoadMode.Lazy);
        Collection<?> items = this.onQuery(parameter,itemClass, orderBy, pagingInfo);
        if(items != null){
            List<T> result = new ArrayList<T>();
            for(Object item : items){
                result.add(EntityUtils.convertEntityType(item, itemClass));
            }
            return result;
        }
        return new ArrayList<T>();
    }

	@Override
	public ValueWrapper get(Object key) {
		Object value = this.getValue(key);
		return new SimpleValueWrapper(value);
	}

	@Override
	public ValueWrapper putIfAbsent(Object key, Object value) {
		ValueWrapper vw = this.get(key);
		if(vw == null){
			this.put(key, value);
			vw = new SimpleValueWrapper(value);
		}
		return vw;
	}

    @Override
    @SuppressWarnings("unchecked")
    public final <T> T getValue(Object key) {
        this.loadAllFromProvider(LoadMode.Lazy);
        Object val = this.onGet(key);
        if(val == null){
            val = this.loadItemFromProvider(key);
        }
        return (T) val;
    }

    @Override
    public final <T> T get(Object key, Class<T> itemClass) {
        Object val = this.get(key);
        return EntityUtils.convertEntityType(val, itemClass);
    }

    @Override
    @SuppressWarnings("unchecked")
    public final <T> Collection<T> gets(Object... keys) {
        this.loadAllFromProvider(LoadMode.Lazy);
        Collection<?> items = this.gets(keys);
        if((items == null || items.isEmpty())){
            items = this.loadItemsFromProvider(keys);
        }
        return (Collection<T>) items;
    }

    @Override
    public final <T> Collection<T> gets(Class<T> itemClass, Object... keys) {
        Collection<Object> items = this.gets(keys);
        if(items != null && !items.isEmpty()){
            List<T> result = new ArrayList<T>();
            for(Object item : items){
                result.add(EntityUtils.convertEntityType(item, itemClass));
            }
        }
        return new ArrayList<T>();
    }
    protected abstract void onReload();
    protected abstract Collection<?> onQuery(Object parameter,Class<?> itemClass,String orderBy,PagingInfo pagingInfo);
    protected abstract Object onGet(Object key);
    protected abstract Collection<?> onGets(Object ... keys);

}
