package com.harvey.w.core.service;

import java.util.List;
import java.util.Map;

import com.harvey.w.core.model.DynamicModelClass;

public interface BacDataService {
    
    List<DynamicModelClass> findItems(String queryName,Map<String,Object> parameters,String condition,String orderBy);
    
}
