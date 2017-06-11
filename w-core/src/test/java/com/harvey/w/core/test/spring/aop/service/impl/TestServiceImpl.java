package com.harvey.w.core.test.spring.aop.service.impl;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;

import com.harvey.w.core.test.spring.aop.service.TestService;

//@Service
public class TestServiceImpl implements TestService {

	@Override
	public List<Integer> getList() {
		return Arrays.asList(1,2,3,4);
	}

}
