package com.harvey.w.boot.test.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;

import com.harvey.w.boot.test.model.SysUserModel;
import com.harvey.w.boot.test.service.SysUserService;
import com.harvey.w.core.service.impl.BaseServiceImpl;

@Service
public class SysUserServiceImpl extends BaseServiceImpl implements SysUserService {

    @Override
    public SysUserModel get(Integer userId) {
        return this.dao.get(SysUserModel.class, userId);
    }

    @Override
    public List<SysUserModel> getAll() {
        return this.dao.getAll(SysUserModel.class);
    }

    @Override
    public SysUserModel save(SysUserModel user) {
        return this.dao.save(user);
    }

}
