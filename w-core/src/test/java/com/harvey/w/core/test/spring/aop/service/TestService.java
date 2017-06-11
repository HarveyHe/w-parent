package com.harvey.w.core.test.spring.aop.service;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;

import com.harvey.w.core.aop.annotation.Aop;

public interface TestService {
	
	@Aop("bean:simpleTraceInterceptor")
	@Cacheable("simpleCache")
	List<Integer> getList();
}
