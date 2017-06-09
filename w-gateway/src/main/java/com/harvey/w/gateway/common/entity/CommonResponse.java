package com.harvey.w.gateway.common.entity;

/**
 * @author harvey
 * @param <T>
 */
public class CommonResponse <T>{
    private String status;
    private T data;

    public void setStatus(String status){
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getData() {

        return data;
    }

}
