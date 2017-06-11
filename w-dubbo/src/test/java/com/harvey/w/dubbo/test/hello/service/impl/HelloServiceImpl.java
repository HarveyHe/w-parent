package com.harvey.w.dubbo.test.hello.service.impl;

import java.util.Date;

import org.springframework.stereotype.Service;

import com.harvey.w.core.service.impl.BaseServiceImpl;
import com.harvey.w.dubbo.test.hello.entity.HelloEntity;
import com.harvey.w.dubbo.test.hello.service.HelloService;

@Service
public class HelloServiceImpl extends BaseServiceImpl implements HelloService {

	@Override
	public String saveHello(HelloEntity entity) {
		log.info(entity);
		return entity.getWord();
	}

	@Override
	public HelloEntity getHello() {
		HelloEntity entity = new HelloEntity();
		entity.setDate(new Date());
		entity.setIsTrue(true);
		entity.setWord("I'am from Service");
		return entity;
	}

}
