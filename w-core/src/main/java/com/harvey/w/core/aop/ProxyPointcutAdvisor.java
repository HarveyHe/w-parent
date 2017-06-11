package com.harvey.w.core.aop;

import org.springframework.aop.Pointcut;
import org.springframework.aop.support.AbstractBeanFactoryPointcutAdvisor;

public class ProxyPointcutAdvisor extends AbstractBeanFactoryPointcutAdvisor {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private InterceptionDescriptorSource descriptorSource;

	private InterceptionSourcePointcut pointcut = new InterceptionSourcePointcut() {

		@Override
		protected InterceptionDescriptorSource getSource() {
			return descriptorSource;
		}

	};

	@Override
	public Pointcut getPointcut() {
		return pointcut;
	}

	public void setDescriptorSource(InterceptionDescriptorSource descriptorSource) {
		this.descriptorSource = descriptorSource;
	}

}
