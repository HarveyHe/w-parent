package com.harvey.w.core.cache;

import java.util.Collection;
import java.util.Map;

import com.harvey.w.core.model.PagingInfo;

/**
 * 统一缓存接口
 * 缓存实现:
 * Ehcache
 * ConcurrentHashMap
 * memcached
 * redis
 * mongoDb
 * infinispan
 * 
 * 扩展实现多级缓存器 L1 L2...
 * @author admin
 *
 */
public interface Cache extends org.springframework.cache.Cache {
    
    /**
     * query cahce list item
     * @param parameter
     * @param orderBy
     * @param pagingInfo
     * @return cached data items
     */
    <T> Collection<T> queryCache(Object parameter,Class<T> itemClass,String orderBy,PagingInfo pagingInfo);
    
    /**
     * get item from cache
     * @param key
     * @return cached data item
     */
    <T> T getValue(Object key);
    
    /**
     * get item from cache and convert to itemClass
     * @param key
     * @param itemClass
     * @return cached data item
     */
    <T> T get(Object key,Class<T> itemClass);
    
    /**
     * get items from cache
     * @param keys
     * @return cached data item
     */
    <T> Collection<T> gets(Object...keys);
    
    /**
     * get items from cache and convert to itemClass
     * @param itemClass
     * @param keys
     * @return cached data item
     */
    <T> Collection<T> gets(Class<T> itemClass,Object ...keys);
    
    /**
     * put item to cache
     * @param key
     * @param value
     */
    void put(Object key,Object value);
    
    /**
     * put multiple items to cache
     * @param elements
     */
    void puts(Map<?, ?> elements);
    
    /**
     * remove item from cache
     * @param key
     */
    void evict(Object key);
    
    /**
     * remove item from cache
     * @param keys
     */
    void evicts(Object...keys);
    
    /**
     * reload data from data provider
     */
    void reload();
    
    /**
     *setter cache data provider
     * @param dataProvider
     */
    void setDataProvider(DataProvider dataProvider);
    
    /**
     * getter cache data provider
     * @return cache data provider
     */
    DataProvider getDataProvider();
    
}
