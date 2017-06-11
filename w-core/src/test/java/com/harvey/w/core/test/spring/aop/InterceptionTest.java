package com.harvey.w.core.test.spring.aop;

import org.junit.Before;

import com.harvey.w.core.boot.Boot;
import com.harvey.w.core.context.Context;
import com.harvey.w.core.test.spring.aop.service.TestService;

public class InterceptionTest {

	@Before
	public void initial() throws Exception {
		//启动 applicationContext
		Boot.main(new String[] { "classpath:/aop/applicationContext-test.xml", "0" });
	}
	
	//@Test
	public void testSimpleTraceInterceptor(){
		TestService testService = Context.getBean(TestService.class);
		System.out.println(testService.getList());
		System.out.println(testService.getList());
		//System.out.println(testService.getList());
	}
}
