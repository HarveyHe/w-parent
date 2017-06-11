package com.harvey.w.core.exception;

/*
 * 并发版本控制异常
 */
public class ConVersionException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    
    public ConVersionException(Object id,Object version) {
        super(String.format("The data id:%s on you submit version:%s has been modified by another user",id,version));
    }

    public ConVersionException(Object id, Object sysVer,Object ver) {
        super(String.format("The id:%s in system version is:%s,but commit version is:%s.",id, sysVer,ver));
    }
    
     
}
