package com.harvey.w.core.spring.security.utils;

import org.springframework.security.authentication.AnonymousAuthenticationToken;

import com.harvey.w.core.context.Context;

public class SecurityUtils {
    public static boolean isAnonymous() {
        return Context.getAuthentication() == null || AnonymousAuthenticationToken.class.isAssignableFrom(Context.getAuthentication().getClass());
    }
}
