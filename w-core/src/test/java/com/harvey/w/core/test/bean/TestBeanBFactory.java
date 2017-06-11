package com.harvey.w.core.test.bean;

import org.springframework.beans.factory.FactoryBean;

public class TestBeanBFactory implements FactoryBean<TestBeanB> {

	private TestBeanB bean = new TestBeanB();
	
	@Override
	public TestBeanB getObject() throws Exception {
		return bean;
	}

	@Override
	public Class<?> getObjectType() {
		return TestBeanB.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}

}
