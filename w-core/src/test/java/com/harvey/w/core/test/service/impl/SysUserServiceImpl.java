package com.harvey.w.core.test.service.impl;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.harvey.w.core.model.UserBaseModel;
import com.harvey.w.core.service.impl.BaseServiceImpl;
import com.harvey.w.core.spring.security.tokenaccess.AccessAuthenticationToken;
import com.harvey.w.core.test.service.SysUserService;

@Service
public class SysUserServiceImpl extends BaseServiceImpl implements SysUserService {

	@Override
	public void authenticate(UserBaseModel user, UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken)
			throws AuthenticationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isTokenAccess(HttpServletRequest request, HttpServletResponse response) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public UserBaseModel autoLogin(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void authenticateToken(UserBaseModel user, AccessAuthenticationToken authentication) throws AuthenticationException {
		// TODO Auto-generated method stub
		
	}

}
