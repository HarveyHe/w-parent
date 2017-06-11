package com.harvey.w.core.aop;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aopalliance.intercept.MethodInvocation;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.harvey.w.core.bean.SpringExpressionBean;

public class InterceptionDescriptorSource implements InitializingBean {

	@Autowired(required = false)
	private InterceptionDescriptorParser interceptionDescriptorParser;

	private SpringExpressionBean spelExpression;

	final Map<Object, InterceptionDescriptors> attributeCache = new ConcurrentHashMap<Object, InterceptionDescriptors>(1024);

	public InterceptionDescriptorSource() {
		this(null, null);
	}

	public InterceptionDescriptorSource(SpringExpressionBean spelExpression, InterceptionDescriptorParser interceptionDescriptorParser) {
		this.spelExpression = spelExpression;
		this.interceptionDescriptorParser = interceptionDescriptorParser;
	}

	public List<InterceptionDescriptor> getInterceptionDescriptors(final MethodInvocation invocation, final Class<?> targetClass) {
		InterceptionDescriptors descriptors = this.getInterceptionDescriptors(targetClass, invocation.getMethod());
		if (CollectionUtils.isEmpty(descriptors)) {
			return null;
		} else if (!descriptors.isHasCondition()) {
			return descriptors;
		}
		InterceptionContext context = new InterceptionContext() {

			@Override
			public Class<?> getTargetClass() {
				return targetClass;
			}

			@Override
			public Object getTarget() {
				return invocation.getThis();
			}

			@Override
			public Method getMethod() {
				return invocation.getMethod();
			}

			@Override
			public Object[] getArgs() {
				return invocation.getArguments();
			}
		};

		List<InterceptionDescriptor> result = new ArrayList<InterceptionDescriptor>(descriptors.size());
		for (InterceptionDescriptor descriptor : descriptors) {
			if (canIntercepting(descriptor, context)) {
				result.add(descriptor);
			}
		}
		return result;
	}

	public InterceptionDescriptors getInterceptionDescriptors(Class<?> clazz, Method method) {
		Object key = genKey(clazz, method);
		InterceptionDescriptors descriptors = this.attributeCache.get(key);
		if (descriptors != null) {
			return descriptors;
		} else {
			descriptors = doParse(clazz, method);
			this.attributeCache.put(key, descriptors);
			return descriptors;
		}
	}

	private boolean canIntercepting(InterceptionDescriptor descriptor, InterceptionContext context) {
		if (StringUtils.isEmpty(descriptor.getCondition())) {
			return true;
		}
		return Boolean.TRUE.equals(this.spelExpression.getValue(descriptor.getCondition(), context, Boolean.class));
	}

	private InterceptionDescriptors doParse(Class<?> clazz, Method method) {
		InterceptionDescriptors descriptors = interceptionDescriptorParser.parseAnnotation(clazz, method);
		return descriptors.isEmpty() ? InterceptionDescriptors.EMPTY_LIST : descriptors;
	}

	private Object genKey(Class<?> clazz, Method method) {
		return new MultiKey(clazz, method);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		if (spelExpression == null) {
			spelExpression = new SpringExpressionBean();
		}
		if (interceptionDescriptorParser == null) {
			this.interceptionDescriptorParser = new DefaultInterceptionDescriptorParser(spelExpression);
		}
	}

	public void setInterceptionDescriptorParser(InterceptionDescriptorParser interceptionDescriptorParser) {
		this.interceptionDescriptorParser = interceptionDescriptorParser;
	}
}
