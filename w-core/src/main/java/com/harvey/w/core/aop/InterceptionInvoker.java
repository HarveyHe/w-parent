package com.harvey.w.core.aop;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.Collection;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.CollectionUtils;

import com.harvey.w.core.utils.ReflectionUtils;

public class InterceptionInvoker implements MethodInterceptor,ApplicationContextAware {

    private ApplicationContext applicationContext;
    private InterceptionDescriptorSource descriptorSource;

    public InterceptionInvoker(InterceptionDescriptorSource descriptorSource) {
        this.descriptorSource = descriptorSource;
    }

    @Override
    public Object invoke(final MethodInvocation invocation) throws Throwable {
        final Class<?> targetClass = ReflectionUtils.getTargetClass(invocation.getThis());
        Collection<InterceptionDescriptor> descriptors = descriptorSource.getInterceptionDescriptors(targetClass, invocation.getMethod());
        if (CollectionUtils.isEmpty(descriptors)) {
            return invocation.proceed();
        }
        InvocationContext context = new InvocationContext(descriptors){

			@Override
			public MethodInvocation invocation() {
				return invocation;
			}

			@Override
			public Class<?> getTargetClass() {
				return targetClass;
			}

			@Override
			public Method getMethod() {
				return invocation.getMethod();
			}

			@Override
			public Object getTarget() {
				return invocation.getThis();
			}

			@Override
			public Object[] getArgs() {
				return invocation.getArguments();
			}

			@Override
			public ApplicationContext getApplicationContext() {
				return applicationContext;
			}
        };
        if(CollectionUtils.isEmpty(context.interceptors())){
            return invocation.proceed();
        }
        InterceptorInvocation invo = new InterceptorInvocation(context);
        return invo.proceed();
    }

    private class InterceptorInvocation implements MethodInvocation {

        private InvocationContext context;
        private int position = 0;

        InterceptorInvocation(InvocationContext interceptionContext) {
            this.context = interceptionContext;
        }

        private MethodInterceptor next(){
            if(position < context.interceptors().size()){
                return context.interceptors().get(position++);
            }
            return null;
        }
        
        @Override
        public Object proceed() throws Throwable {
            MethodInterceptor invocation = this.next();
            if(invocation != null){
                return invocation.invoke(this);
            }
            return context.invocation().proceed();
        }

        @Override
        public Object[] getArguments() {
            return context.getArgs();
        }

        @Override
        public Object getThis() {
            return context.getTarget();
        }

        @Override
        public AccessibleObject getStaticPart() {
            return context.invocation().getStaticPart();
        }

        @Override
        public Method getMethod() {
            return context.getMethod();
        }

    }

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}
}
