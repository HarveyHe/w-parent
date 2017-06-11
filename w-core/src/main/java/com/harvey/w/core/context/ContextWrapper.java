package com.harvey.w.core.context;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;

import com.harvey.w.core.model.UserBaseModel;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.Authentication;
import org.springframework.web.context.WebApplicationContext;

public interface ContextWrapper {

    WebApplicationContext getServletApplicationContext(String servletName);

    ApplicationContext getContext();

    HttpServletRequest getRequest();

    <T> T getBean(String beanId);

    <T> T getBean(Class<T> type);

    Authentication getAuthentication();

    <T extends UserBaseModel> T getCurrentUser();

    <T extends UserBaseModel> T checkCurrentUser();

    ServletContext getServletContext();

    String getContextPath();

    ContextWrapper Instance = new ContextWrapper() {

        @Override
        public WebApplicationContext getServletApplicationContext(String servletName) {
            return Context.getServletApplicationContext(servletName);
        }

        @Override
        public ApplicationContext getContext() {
            return Context.getContext();
        }

        @Override
        public HttpServletRequest getRequest() {
            return Context.getRequest();
        }

        @Override
        public <T> T getBean(String beanId) {
            return Context.getBean(beanId);
        }

        @Override
        public <T> T getBean(Class<T> type) {
            return Context.getBean(type);
        }

        @Override
        public Authentication getAuthentication() {
            return Context.getAuthentication();
        }

        @Override
        public <T extends UserBaseModel> T getCurrentUser() {
            return Context.getCurrentUser();
        }

        @Override
        public <T extends UserBaseModel> T checkCurrentUser() {
            return Context.getCurrentUser();
        }

        @Override
        public ServletContext getServletContext() {
            return Context.getServletContext();
        }

        @Override
        public String getContextPath() {
            return Context.getContextPath();
        }

    };
}