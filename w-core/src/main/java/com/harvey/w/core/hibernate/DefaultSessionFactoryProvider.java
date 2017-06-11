package com.harvey.w.core.hibernate;

import org.hibernate.SessionFactory;

public class DefaultSessionFactoryProvider implements SessionFactoryProvider {

    private SessionFactory sessionFactory;

    public DefaultSessionFactoryProvider(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public SessionFactory getSessionFactory() {
        return sessionFactory;
    }

}
