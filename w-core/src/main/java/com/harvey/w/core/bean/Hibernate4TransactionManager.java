package com.harvey.w.core.bean;

import javax.sql.DataSource;

import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.transaction.TransactionDefinition;

@SuppressWarnings("serial")
public class Hibernate4TransactionManager extends HibernateTransactionManager {
	static final ThreadLocal<DataSource> DATASOURCES = new ThreadLocal<>();
	
	private DataSource realDataSource;
	
	@Override
    protected void doBegin(Object transaction, TransactionDefinition definition) {
	    try{
	    	DATASOURCES.set(realDataSource);
	    	super.doBegin(transaction, definition);
	    }finally{
	    	DATASOURCES.remove();
	    }
    }

	public DataSource getRealDataSource() {
		return realDataSource;
	}

	public void setRealDataSource(DataSource realDataSource) {
		this.realDataSource = realDataSource;
	}
	
	
}
