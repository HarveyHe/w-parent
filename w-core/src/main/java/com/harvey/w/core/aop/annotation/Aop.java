/**
 * 
 */
package com.harvey.w.core.aop.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *如果注解声明为类，则对此类的所有方法调用进行拦截
 *如果注解声明为方法，则对此方法调用进行拦截
 *
 *已知可用拦截器有(Spring提供):
 * {@link org.springframework.aop.interceptor.SimpleTraceInterceptor}
 * {@link org.springframework.aop.interceptor.PerformanceMonitorInterceptor}
 * {@link org.springframework.aop.interceptor.JamonPerformanceMonitorInterceptor}
 * {@link org.springframework.aop.interceptor.ExposeInvocationInterceptor}
 * {@link org.springframework.aop.interceptor.DebugInterceptor}
 * {@link org.springframework.aop.interceptor.CustomizableTraceInterceptor}
 * {@link org.springframework.aop.interceptor.ConcurrencyThrottleInterceptor}
 * {@link org.springframework.aop.interceptor.AsyncExecutionInterceptor}
 */
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface Aop {
    /**
     * 拦截器
     * 拦截器为：org.aopalliance.intercept.MethodInterceptor
     * 配置格式,Spring Bean格式：bean:beanName
     * 指定类格式：com.xxx.AopInterceptor
     */
	String[] value() default {};
    
    /**
     * 拦截生效条件,支持 SPEL表达式，如:#Config.get('app.isEnableInterceptor')
     */
    String condition() default "";
    
    /**
     * 调用时判断条件是否执行拦截
     */
    String runtimeCondition() default "";
}
