package com.harvey.w.gateway.common.entity;

/**
 * @author harvey
 * @param <T>
 */
public class CommonRequest<T> {

    private T[] request;

    public void setRequest(T[] request) {
        this.request = request;
    }

    public T[] getRequest() {

        return request;
    }
}
