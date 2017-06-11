package com.harvey.w.core.cache;

public enum LoadMode {
    /**
     * 按需加载至缓存
     */
    ByNeed,
    
    /**
     * 初始化时加载所有数据至缓存
     */
    OnInit, 
    
    /**
     * 延迟加载所有数据至缓存,在获取数据时加载
     */
    Lazy
}
