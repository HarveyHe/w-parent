package com.harvey.w.core.test.hibernate;

import java.util.Map;

import org.springframework.stereotype.Component;

import com.harvey.w.core.hibernate.DataChangesListener;

@Component
public class DataChangesEventListener implements DataChangesListener {

    private Object model;
    private Map<String,Object> state;
    private Map<String,Object> oldState;
    private ThreadLocal<String> threadLocal = new ThreadLocal<>();
    
    public ThreadLocal<String> getThreadLocal() {
        return threadLocal;
    }

    public Object getModel() {
        return model;
    }

    public Map<String, Object> getState() {
        return state;
    }

    public Map<String, Object> getOldState() {
        return oldState;
    }

    @Override
    public void onChanges(Object model, Map<String, Object> state, Map<String, Object> oldState) {
        this.threadLocal.set(model.getClass().getName());
        try{
            //calling
        }finally{
            this.threadLocal.remove();
        }
        this.model=model;
        this.state=state;
        this.oldState = oldState;
    }

}
