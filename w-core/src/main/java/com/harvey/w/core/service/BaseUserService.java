package com.harvey.w.core.service;

import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


public interface BaseUserService extends UserDetailsService {
  
  abstract UserDetails loadUserDetials(String username,Map<String,String> params) throws UsernameNotFoundException;
}
