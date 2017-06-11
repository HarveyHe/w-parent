package com.harvey.w.core.beetl;

public class ParseException extends RuntimeException {
    
    public ParseException(String message){
        super(message);
    }
    
    public ParseException(String message,Throwable throwable){
        super(message,throwable);
    }
}
