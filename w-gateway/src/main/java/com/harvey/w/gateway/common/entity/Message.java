package com.harvey.w.gateway.common.entity;

/**
 * @author harvey
 * @param <T>
 */
public class Message<T> {

	private Header header;

	private T payload;

	public Header getHeader() {
		return header;
	}

	public void setHeader(Header header) {
		this.header = header;
	}

	public T getPayload() {
		return payload;
	}

	public void setPayload(T payload) {
		this.payload = payload;
	}

}
