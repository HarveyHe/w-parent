package com.harvey.w.core.aop;

import java.lang.reflect.Method;

import org.springframework.aop.support.StaticMethodMatcherPointcut;
import org.springframework.util.CollectionUtils;

public abstract class InterceptionSourcePointcut extends StaticMethodMatcherPointcut {

	@Override
	public boolean matches(Method method, Class<?> targetClass) {
		InterceptionDescriptorSource src = this.getSource();
		return src != null && !CollectionUtils.isEmpty(src.getInterceptionDescriptors(targetClass, method));
	}

	protected abstract InterceptionDescriptorSource getSource();
}
