package com.harvey.w.core.hibernate;

import java.util.Map;

public interface DataChangesListener {
    void onChanges(Object model,Map<String,Object> state,Map<String,Object> oldState);
}
