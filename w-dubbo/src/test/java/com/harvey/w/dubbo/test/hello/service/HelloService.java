package com.harvey.w.dubbo.test.hello.service;

import com.harvey.w.core.service.BaseService;
import com.harvey.w.dubbo.test.hello.entity.HelloEntity;

public interface HelloService extends BaseService {
	
	String saveHello(HelloEntity entity);
	
	HelloEntity getHello();
	
}
