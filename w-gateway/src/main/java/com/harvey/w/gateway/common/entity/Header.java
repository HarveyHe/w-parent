package com.harvey.w.gateway.common.entity;

import com.gsst.eaf.core.model.PagingInfo;

/**
 * @author harvey
 */
public class Header {

	private Integer appId;
	private String accessToken;
	private PagingInfo pagingInfo;

	public void setPagingInfo(PagingInfo pagingInfo) {
		this.pagingInfo = pagingInfo;
	}

	public PagingInfo getPagingInfo() {

		return pagingInfo;
	}

	public Integer getAppId() {
		return appId;
	}

	public void setAppId(Integer appId) {
		this.appId = appId;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

}
