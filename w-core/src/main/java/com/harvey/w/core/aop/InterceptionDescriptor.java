package com.harvey.w.core.aop;

import org.aopalliance.intercept.MethodInterceptor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;


public class InterceptionDescriptor {
	private String beanName;
	private String typeName;
	private MethodInterceptor interceptor;
	private String condition;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public MethodInterceptor getInterceptor(InvocationContext context) {
		if (interceptor != null) {
			return interceptor;
		}
		if (!StringUtils.isEmpty(beanName)) {
			interceptor = context.getApplicationContext().getBean(beanName, MethodInterceptor.class);
		}
		if (!StringUtils.isEmpty(typeName)) {
			try {
				interceptor = BeanUtils.instantiateClass(Class.forName(typeName), MethodInterceptor.class);
			} catch (Exception ex) {
				throw new RuntimeException(ex);
			}
		}
		return interceptor;
	}

	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
	}
}
