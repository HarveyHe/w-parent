package com.harvey.w.core.test.service;

import com.harvey.w.core.service.BaseService;
import com.harvey.w.core.service.UserBaseService;
import com.harvey.w.core.spring.security.tokenaccess.service.TokenizationService;

public interface SysUserService extends BaseService,UserBaseService,TokenizationService {

}
