package com.harvey.w.dubbo.test.client;

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.harvey.w.dubbo.test.hello.entity.HelloEntity;
import com.harvey.w.dubbo.test.hello.service.HelloService;

@Component
public class ConsumeClient implements InitializingBean {
	private static final Log log = LogFactory.getLog(ConsumeClient.class);
	
	@Autowired
	private HelloService helloService;

	@Override
	public void afterPropertiesSet() throws Exception {

	}
	
	
}
