package com.harvey.w.core.test.bean;

import org.springframework.stereotype.Component;

@Component
public class TestBean {
	
	public TestBean(){
		System.out.println("test");
	}
	
	private String say;
	
	public void setSay(String say){
		this.say = say;
	}
	
	public String getSay(){
		return say;
	}
}
