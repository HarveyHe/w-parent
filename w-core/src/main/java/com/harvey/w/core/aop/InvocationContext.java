package com.harvey.w.core.aop;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import org.springframework.context.ApplicationContext;
import org.springframework.core.OrderComparator;

public abstract class InvocationContext implements InterceptionContext {
    private List<MethodInterceptor> interceptors;

   public InvocationContext(Collection<InterceptionDescriptor> descriptors) {
        initialInvocation(descriptors);
    }

    private void initialInvocation(Collection<InterceptionDescriptor> descriptors) {
        for (InterceptionDescriptor descriptor : descriptors) {
            MethodInterceptor interceptor = descriptor.getInterceptor(this);
            if (interceptor != null) {
                if (interceptors == null) {
                    interceptors = new ArrayList<>(1);
                }
                interceptors.add(interceptor);
            }
        }
        if(interceptors != null){
        	OrderComparator.sort(interceptors);
        }
    }

    public abstract MethodInvocation invocation();

    List<MethodInterceptor> interceptors() {
        return this.interceptors;
    }
    
	public abstract ApplicationContext getApplicationContext();
}
