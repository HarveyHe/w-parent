package com.harvey.w.core.model;

import java.util.Date;

public interface ModifyingInfo {
    String getModifier();
    
    void setModifier(String modifier);
    
    Date getModifyTime();
    
    void setModifyTime(Date modifyTime);
}
