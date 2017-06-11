package com.harvey.w.core.spring.security.tokenaccess.service;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.harvey.w.core.model.UserBaseModel;
import org.springframework.security.core.AuthenticationException;

import com.harvey.w.core.spring.security.tokenaccess.AccessAuthenticationToken;

public interface TokenizationService {

    /**
     * 判断是否为token访问
     * @param request http请求
     * @param response http响应
     * @return true即为token访问
     */
    boolean isTokenAccess(HttpServletRequest request,HttpServletResponse response);
    
    /**
     * 根据token自动登录
     * @param request http请求
     * @param response http响应
     * @return 当前用户对象
     * @throws AuthenticationException
     */
    UserBaseModel autoLogin(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException;

    /**
     * 用户授权检测
     * @param user
     * @param authentication
     * @throws AuthenticationException
     */
    void authenticateToken(UserBaseModel user, AccessAuthenticationToken authentication) throws AuthenticationException;
}
