package com.harvey.w.dubbo.test.hello.entity;

import java.util.Date;

public class HelloEntity {
	
	private String word;
	private Date date;
	private Boolean isTrue;
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	public Boolean getIsTrue() {
		return isTrue;
	}
	public void setIsTrue(Boolean isTrue) {
		this.isTrue = isTrue;
	}
	@Override
	public String toString() {
		return "[word=" + word + ", date=" + date + ", isTrue=" + isTrue + "]";
	}
}
