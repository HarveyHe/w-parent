package com.harvey.w.core.aop;

import java.lang.reflect.Method;

public interface InterceptionContext {
	Class<?> getTargetClass();

	Method getMethod();

	Object getTarget();

	Object[] getArgs();
}
