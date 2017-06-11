package com.harvey.w.core.hibernate;

import java.util.List;

import org.hibernate.event.service.spi.EventListenerRegistry;
import org.hibernate.event.spi.EventType;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

public class SessionFactoryBeanProcessor implements InitializingBean {

    @Autowired(required = false)
    private List<DataChangesListener> dataChangesListeners;

    @Autowired(required = false)
    private List<SessionFactoryImpl> sessionFactories;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (CollectionUtils.isEmpty(dataChangesListeners) || CollectionUtils.isEmpty(sessionFactories)) {
            return;
        }
        for (SessionFactoryImpl sessionFactory : sessionFactories) {
            EventListenerRegistry registry = sessionFactory.getServiceRegistry().getService(EventListenerRegistry.class);
            HibernateDataChangeListeners listeners = new HibernateDataChangeListeners(this.dataChangesListeners);
            registry.appendListeners(EventType.PRE_INSERT, listeners);
            registry.appendListeners(EventType.PRE_UPDATE, listeners);
        }
    }

}
