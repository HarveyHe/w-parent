package com.harvey.w.core.spring.security.handler;

import javax.servlet.http.HttpServletRequest;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.harvey.w.core.utils.SortOrder;

public interface AuthAccessDeniedHandler extends AccessDeniedHandler,SortOrder {
    
    boolean isSupportHandler(HttpServletRequest request, AccessDeniedException exception);
    
}
