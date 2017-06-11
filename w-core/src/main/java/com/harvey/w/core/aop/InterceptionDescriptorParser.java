package com.harvey.w.core.aop;

import java.lang.reflect.Method;

public interface InterceptionDescriptorParser {

	InterceptionDescriptors parseAnnotation(Class<?> clazz,Method method);
}
