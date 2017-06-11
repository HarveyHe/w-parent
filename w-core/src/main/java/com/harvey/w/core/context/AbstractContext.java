package com.harvey.w.core.context;

import javax.servlet.ServletContext;

import org.springframework.context.ApplicationContext;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.DispatcherServlet;

import com.harvey.w.core.model.UserBaseModel;

public abstract class AbstractContext {
    private static ApplicationContext context;
    private static CurrentUserDelegate userDelegate;
    private static ServletContext servletContext;

    public static WebApplicationContext getServletApplicationContext(String servletName) {
        return WebApplicationContextUtils.getWebApplicationContext(servletContext, DispatcherServlet.SERVLET_CONTEXT_PREFIX + servletName);
    }

    public static ApplicationContext getContext() {
        return context;
    }

    public static void setContext(ApplicationContext context) {
        AbstractContext.context = context;
    }

    @SuppressWarnings("unchecked")
    public static <T> T getBean(String beanId) {
        return (T) getContext().getBean(beanId);
    }

    public static <T> T getBean(Class<T> type) {
        return getContext().getBean(type);
    }

    public static <T extends UserBaseModel> T getCurrentUser() {
        if (userDelegate == null) {
            synchronized (CurrentUserDelegate.class) {
                if (userDelegate == null) {
                    try {
                        userDelegate = getBean(CurrentUserDelegate.class);
                    } catch (Exception ex) {

                    }
                    if (userDelegate == null) {
                        userDelegate = new DefaultCurrentUserDelegate();
                    }
                }
            }
        }
        return userDelegate.getCurrentUser();
    }

    public static ServletContext getServletContext() {

        return servletContext;
    }

    public static void setServletContext(ServletContext servletContext) {
        AbstractContext.servletContext = servletContext;
    }
}
