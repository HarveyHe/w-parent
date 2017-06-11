package com.harvey.w.core.hibernate;

import org.hibernate.SessionFactory;

public interface SessionFactoryProvider {
    SessionFactory getSessionFactory();
}
