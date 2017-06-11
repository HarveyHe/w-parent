package com.harvey.w.core.bean;

import com.harvey.w.core.context.Context;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.support.TransactionTemplate;

public class TransactionTemplateBean extends TransactionTemplate {

    public TransactionTemplateBean() {
        this(Context.getBean(PlatformTransactionManager.class));
        this.setPropagationBehavior(PROPAGATION_REQUIRES_NEW);
    }

    public TransactionTemplateBean(PlatformTransactionManager transactionManager, TransactionDefinition transactionDefinition) {
        super(transactionManager, transactionDefinition);
    }

    public TransactionTemplateBean(PlatformTransactionManager transactionManager) {
        super(transactionManager);
    }
    
}
