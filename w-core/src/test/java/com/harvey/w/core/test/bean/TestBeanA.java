package com.harvey.w.core.test.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


public class TestBeanA {
	
	@Autowired
	private TestBean bean;
	
	@Autowired
	private List<TestBeanB> beanB;
	
	private String say = TestBeanA.class.getName();
	
	public void setSay(String say){
		this.say = say;
	}
	
	public String getSay(){
		return say;
	}

	public TestBean getBean() {
		return bean;
	}

	public void setBean(TestBean bean) {
		this.bean = bean;
	}

	public List<TestBeanB> getBeanB() {
		return beanB;
	}

	public void setBeanB(List<TestBeanB> beanB) {
		this.beanB = beanB;
	}
}
