package com.harvey.w.core.test.spring;

import static org.junit.Assert.assertTrue;

import javax.servlet.ServletException;

import org.junit.After;
import org.junit.Before;
import org.springframework.context.ApplicationContext;

import com.harvey.w.core.context.Context;
import com.harvey.w.core.test.bean.TestBean;
import com.harvey.w.core.test.support.JettyApplication;

public class ApplicationTests {

	private JettyApplication application;

	@Before
	public void setUp() throws Exception {
		application = new JettyApplication();
		application.startup();

	}

	@After
	public void destory() throws Exception {
		application.shutdown();
	}

	//@Test
	public void testListener() throws ServletException {

		ApplicationContext applicationContext = Context.getContext();

		Object bean = applicationContext.getBean("testBean");

		assertTrue(bean instanceof TestBean);
	}

	/*
	 * private static class MyContextLoaderInitializer extends
	 * AbstractContextLoaderInitializer {
	 * 
	 * @Override protected WebApplicationContext createRootApplicationContext()
	 * { StaticWebApplicationContext rootContext = new
	 * StaticWebApplicationContext(); rootContext.registerSingleton(BEAN_NAME,
	 * MyBean.class); return rootContext; } }
	 */

}
