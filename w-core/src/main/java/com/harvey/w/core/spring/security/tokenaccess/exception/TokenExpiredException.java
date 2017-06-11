package com.harvey.w.core.spring.security.tokenaccess.exception;

import org.springframework.security.core.AuthenticationException;

public class TokenExpiredException extends AuthenticationException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    public TokenExpiredException(String msg) {
        super(msg);
    }

}
