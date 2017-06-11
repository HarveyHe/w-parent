package com.harvey.w.core.spring.security.tokenaccess.provider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.harvey.w.core.utils.SortOrder;

public interface TokenAuthenticationProvider extends SortOrder {
    
    boolean isSupported(HttpServletRequest request,HttpServletResponse response);
    
    Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;
    
}
