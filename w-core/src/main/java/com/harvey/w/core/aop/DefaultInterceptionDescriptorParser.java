package com.harvey.w.core.aop;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import com.harvey.w.core.aop.annotation.Aop;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.BridgeMethodResolver;
import org.springframework.util.ClassUtils;

import com.harvey.w.core.bean.SpringExpressionBean;

public class DefaultInterceptionDescriptorParser implements InterceptionDescriptorParser {

	private SpringExpressionBean spelExpression;

	public DefaultInterceptionDescriptorParser() {
	}

	public DefaultInterceptionDescriptorParser(SpringExpressionBean spelExpression) {
		this.spelExpression = spelExpression;
	}

	@Override
	public InterceptionDescriptors parseAnnotation(Class<?> clazz, Method method) {
		if (!Modifier.isPublic(method.getModifiers())) {
			return null;
		}
		Method specificMethod = ClassUtils.getMostSpecificMethod(method, clazz);
		specificMethod = BridgeMethodResolver.findBridgedMethod(specificMethod);

		List<Aop> anns = new ArrayList<Aop>(2);
		doGetAnnotations(anns, specificMethod, Aop.class);
		doGetAnnotations(anns, specificMethod.getDeclaringClass(), Aop.class);
		if (specificMethod != method) {
			doGetAnnotations(anns, method, Aop.class);
			doGetAnnotations(anns, method.getDeclaringClass(), Aop.class);
		}
		if (anns.size() > 0) {
			InterceptionContext context = this.getInterceptionContext(clazz, method);
			InterceptionDescriptors source = new InterceptionDescriptors(2);
			doParse(source, anns, context);
			return source;
		}
		return InterceptionDescriptors.EMPTY_LIST;
	}

	private void doParse(List<InterceptionDescriptor> source, List<Aop> aops, InterceptionContext context) {
		for (Aop aop : aops) {
			if (ArrayUtils.isEmpty(aop.value()) || !canIntecepting(aop.condition(), context)) {
				continue;
			}
			for (String interceptor : aop.value()) {
				InterceptionDescriptor descriptor = this.parseInterceptionDescriptor(interceptor, aop.runtimeCondition());
				if (descriptor != null) {
					source.add(descriptor);
				}
			}
		}
	}

	private InterceptionDescriptor parseInterceptionDescriptor(String interceptor, String runtimeCondition) {
		if (StringUtils.isEmpty(interceptor)) {
			return null;
		}
		if (interceptor.startsWith("bean:")) {
			interceptor = interceptor.substring(5);
			if (StringUtils.isEmpty(interceptor)) {
				return null;
			}
			InterceptionDescriptor descriptor = new InterceptionDescriptor();
			descriptor.setBeanName(interceptor);
			descriptor.setCondition(runtimeCondition);
			return descriptor;
		} else if (ClassUtils.isPresent(interceptor, this.getClass().getClassLoader())) {
			InterceptionDescriptor descriptor = new InterceptionDescriptor();
			descriptor.setTypeName(interceptor);
			descriptor.setCondition(runtimeCondition);
			return descriptor;
		}
		return null;
	}

	private InterceptionContext getInterceptionContext(final Class<?> clazz, final Method method) {
		return new InterceptionContext() {

			@Override
			public Class<?> getTargetClass() {
				return clazz;
			}

			@Override
			public Object getTarget() {
				return null;
			}

			@Override
			public Method getMethod() {
				return method;
			}

			@Override
			public Object[] getArgs() {
				return ArrayUtils.EMPTY_OBJECT_ARRAY;
			}
		};
	}

	private <T extends Annotation> void doGetAnnotations(List<T> anns, AnnotatedElement ae, Class<T> annotationType) {

		// look at raw annotation
		T ann = ae.getAnnotation(annotationType);
		if (ann != null) {
			anns.add(ann);
		}

		// scan meta-annotations
		for (Annotation metaAnn : ae.getAnnotations()) {
			ann = metaAnn.annotationType().getAnnotation(annotationType);
			if (ann != null) {
				anns.add(ann);
			}
		}
	}

	private boolean canIntecepting(String condition, InterceptionContext context) {
		if (StringUtils.isEmpty(condition)) {
			return true;
		}
		return Boolean.TRUE.equals(this.spelExpression.getValue(condition, context, Boolean.class));
	}

	public SpringExpressionBean getSpelExpression() {
		return spelExpression;
	}

	public void setSpelExpression(SpringExpressionBean spelExpression) {
		this.spelExpression = spelExpression;
	}
}
