package com.harvey.w.core.cache;

import java.util.Map;


public interface DataProvider {
    
    LoadMode mode();
    
    Map<?,?> getAllToCache();
    
    Object getItemToCache(Object key);
    
    Map<?,?> getItemsToCache(Object...keys);
}
