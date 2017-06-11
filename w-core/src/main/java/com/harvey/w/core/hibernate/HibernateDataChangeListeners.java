package com.harvey.w.core.hibernate;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.event.spi.AbstractPreDatabaseOperationEvent;
import org.hibernate.event.spi.PreInsertEvent;
import org.hibernate.event.spi.PreInsertEventListener;
import org.hibernate.event.spi.PreUpdateEvent;
import org.hibernate.event.spi.PreUpdateEventListener;

class HibernateDataChangeListeners implements PreInsertEventListener, PreUpdateEventListener {

    private List<DataChangesListener> listeners;
    
    public HibernateDataChangeListeners(List<DataChangesListener> listeners) {
        this.listeners = listeners;
    }

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    @Override
    public boolean onPreUpdate(PreUpdateEvent event) {
        this.fireChangesEvent(event.getState(), event.getOldState(), event);
        return false;
    }

    @Override
    public boolean onPreInsert(PreInsertEvent event) {
        this.fireChangesEvent(event.getState(), null, event);
        return false;
    }
    
    private void fireChangesEvent(Object[] state,Object[] oldState,AbstractPreDatabaseOperationEvent event) {
        Map<String,Object> stateMap = new LinkedHashMap<String,Object>();
        Map<String,Object> oldStateMap = new LinkedHashMap<String,Object>();
        String[] fieldNames = event.getPersister().getPropertyNames();
        for(int i = 0 ; i < fieldNames.length ; i++) {
            String name = fieldNames[i];
            if(oldState == null) {
                stateMap.put(name, state[i]);
            }else if(!equals(state[i],oldState[i])){
                stateMap.put(name, state[i]);
                oldStateMap.put(name, oldState[i]);
            }
        }
        if(!stateMap.isEmpty()) {
            for(DataChangesListener listener : this.listeners) {
                listener.onChanges(event.getEntity(), stateMap, oldStateMap);
            }
        }
    }
    
    private static boolean equals(Object obj1,Object obj2) {
        if(obj1 == obj2 || (obj1 == null && obj2 == null)) {
            return true;
        } else if(obj1 != null) {
            return obj1.equals(obj2);
        }else if(obj2 != null) {
            return obj2.equals(obj1);
        }
        return false;
    }
}
