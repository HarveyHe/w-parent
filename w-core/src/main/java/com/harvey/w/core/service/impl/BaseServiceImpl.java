package com.harvey.w.core.service.impl;

import com.harvey.w.core.dao.UniversalDao;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

import com.harvey.w.core.dao.NativeSqlDao;
import com.harvey.w.core.service.BaseService;

public abstract class BaseServiceImpl implements BaseService {
    
    protected Log log = LogFactory.getLog(this.getClass());

    @Autowired
    protected UniversalDao dao;

    @Autowired
    protected NativeSqlDao sqlDao;
    
    protected void setRollbackOnly() {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    }
}
