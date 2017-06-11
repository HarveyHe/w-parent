package com.harvey.w.core.test.bean;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


public class TestBeanB {
	
	@Autowired
	private List<TestBean> beans;
	
	private String say;
	
	public void setSay(String say){
		this.say = say;
	}
	
	public String getSay(){
		return say;
	}
}
