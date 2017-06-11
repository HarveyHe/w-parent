package com.harvey.w.core.test.service;

import java.util.List;

import com.harvey.w.core.service.BaseService;
import com.harvey.w.core.test.model.BacTestModel;

public interface BacTestService extends BaseService {
    
    BacTestModel get(Integer id);
    
    List<BacTestModel> getAll();
    
    BacTestModel save(BacTestModel model);
    
    void doConAccess(int bacId,int times);
    
    void saveAll(List<BacTestModel> models);
    
}
