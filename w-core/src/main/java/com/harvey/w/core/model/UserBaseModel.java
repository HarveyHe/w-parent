package com.harvey.w.core.model;

import java.io.Serializable;
import java.util.Map;

import org.springframework.security.core.userdetails.UserDetails;

public interface UserBaseModel extends UserDetails {

    /**
     * 获取User Id
     * @return User Id
     */
    Serializable primeryKeyValue();
    
    /**
     * 储存附加数据
     * @return 附加数据
     */
    Map<Object,Object> getAttribute();
}
