package com.harvey.w.boot.test.service;

import java.util.List;

import com.harvey.w.boot.test.model.SysUserModel;
import com.harvey.w.core.service.BaseService;

public interface SysUserService extends BaseService {
    SysUserModel get(Integer userId);

    List<SysUserModel> getAll();

    SysUserModel save(SysUserModel user);
}
