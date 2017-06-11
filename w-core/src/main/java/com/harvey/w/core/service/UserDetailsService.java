package com.harvey.w.core.service;

import com.harvey.w.core.model.UserBaseModel;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;

public interface UserDetailsService {
    abstract void authenticate(
            UserBaseModel user,
            UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
            throws AuthenticationException;
}
