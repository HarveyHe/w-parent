package com.harvey.w.core.test.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.harvey.w.core.service.impl.BaseServiceImpl;
import com.harvey.w.core.test.model.BacTestModel;
import com.harvey.w.core.test.service.BacTestService;

@Service
public class BacTestServiceImpl extends BaseServiceImpl implements BacTestService {

    @Override
    public BacTestModel get(Integer id) {
        return this.dao.get(BacTestModel.class, id);
    }

    @Override
    public List<BacTestModel> getAll() {
        return this.dao.getAll(BacTestModel.class);
    }

    @Override
    public BacTestModel save(BacTestModel model) {
        return this.dao.save(model);
    }

    @Override
    public void saveAll(List<BacTestModel> models) {
        this.dao.saveAll(models);
    }

    @Override
    public void doConAccess(int bacId, int times) {
        try {
            Thread.sleep(1500l);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        BacTestModel model = this.dao.get(BacTestModel.class, bacId);
        log.warn("times: " + times + " bacValue is:" + model.getBacValue());
        model.setBacValue(model.getBacValue() - 1);
        this.save(model);
    }

}
