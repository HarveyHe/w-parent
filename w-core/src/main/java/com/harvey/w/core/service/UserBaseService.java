package com.harvey.w.core.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface UserBaseService extends UserDetailsService {
    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;
}
