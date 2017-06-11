package com.harvey.w.core.dao;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.harvey.w.core.dao.utils.HibernateUtils;
import com.harvey.w.core.hibernate.DefaultSessionFactoryProvider;
import com.harvey.w.core.hibernate.HibernateSessionFactoryProvider;
import com.harvey.w.core.hibernate.SessionFactoryProvider;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.internal.AbstractQueryImpl;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;




import com.harvey.w.core.context.AbstractContext;
import com.harvey.w.core.dao.utils.QueryUtils;

public abstract class AbstractHibernateDao implements HibernateDao, InitializingBean {

    private Date dbDate;
    private Long timeForLastDbDate = Long.valueOf(0);
    private String selectGUIDString;
    private SessionFactoryProvider factoryProvider;

    @Override
    public SessionFactory getSessionFactory() {
        return factoryProvider.getSessionFactory();
    }

    @Override
    public Criteria createCriteria(Class<?> modelClass) {
        return this.getSessionFactory().getCurrentSession().createCriteria(modelClass);
    }

    @Override
    public SQLQuery createSQLQuery(String sql) {
        return getSessionFactory().getCurrentSession().createSQLQuery(sql);
    }

    @Override
    public void setSessionFactory(SessionFactory sessionFactory) {
        this.factoryProvider = new DefaultSessionFactoryProvider(sessionFactory);
    }

    @Override
    public void flush() {
        this.getSessionFactory().getCurrentSession().flush();
    }

    @Override
    public void evict(Object model) {
    	if(model != null) {
    		this.getSessionFactory().getCurrentSession().evict(model);
    	}
    }

    @Override
    public void evicts(Collection<?> models) {
    	Session session = this.getSessionFactory().getCurrentSession();
        for (Object model : models) {
            if(model != null) {
            	session.evict(model);
            }
        }
    }

    @Override
    public Date getSysDate() {
        if (System.currentTimeMillis() - this.timeForLastDbDate > 1000) {
            synchronized (this.timeForLastDbDate) {
                if (System.currentTimeMillis() - this.timeForLastDbDate > 1000) {
                    Dialect dialect = ((SessionFactoryImplementor) this.getSessionFactory()).getDialect();
                    String sql = dialect.getCurrentTimestampSelectString();
                    int fromIndex = sql.indexOf(" from ");
                    if (fromIndex == -1) {
                        sql = sql + " as SYSDATE__";
                    } else {
                        sql = sql.substring(0, fromIndex) + " as SYSDATE__" + sql.substring(fromIndex);
                    }
                    SQLQuery queryObject = this.getSessionFactory().getCurrentSession().createSQLQuery(sql);
                    queryObject.addScalar("SYSDATE__", StandardBasicTypes.TIMESTAMP);
                    this.dbDate = new Date(((Timestamp) queryObject.uniqueResult()).getTime());
                    this.timeForLastDbDate = System.currentTimeMillis();
                }
            }
        }
        return this.dbDate;
    }

    protected void setParameter(Query query, Object... args) {
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                query.setParameter(i, args[i]);
            }
        }
    }

    protected void setParameter(Query query, Map<String, Object> parameters) {
        if (parameters != null) {
            for (Map.Entry<String, Object> parameter : parameters.entrySet()) {
                query.setParameter(parameter.getKey(), parameter.getValue());
            }
        }
    }

    protected int queryRowCountBySql(String sql, Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues) {
        SessionFactory sessionFactory = this.getSessionFactory();
        StringBuilder query = new StringBuilder(sql);
    	if(!StringUtils.isEmpty(extraSqlCondition)){
    		StringBuilder condition = new StringBuilder(extraSqlCondition);
    		HibernateUtils.toNamedParameters(sessionFactory, condition);
    		QueryUtils.addExtraConditions(query,condition.toString());
    		HibernateUtils.arrayParamsToMap(parameters, parameterValues);
    	}        
    	query.insert(0, "select count(*) as COUNT__ from (");
    	query.append(") T__COUNT__");
        //StringBuilder query = new StringBuilder("select count(*) as COUNT__ from (").append(sql).append(") T__COUNT__");
        SQLQuery queryObject = this.prepareSQLQuery(sessionFactory.getCurrentSession(), query, new HashMap<>(parameters), null, null, null);
        queryObject.addScalar("COUNT__", StandardBasicTypes.INTEGER);
        return (Integer) queryObject.uniqueResult();
    }

    protected int queryRowCountBySql(String sql, Object[] parameters) {
        SessionFactory sessionFactory = this.getSessionFactory();
        StringBuilder query = new StringBuilder("select count(*) as COUNT__ from (").append(sql).append(") T__COUNT__");
        SQLQuery queryObject = this.prepareSQLQuery(sessionFactory.getCurrentSession(), query, parameters);
        queryObject.addScalar("COUNT__", StandardBasicTypes.INTEGER);
        return (Integer) queryObject.uniqueResult();
    } 

    protected String getSelectGUIDString() {
        if (this.selectGUIDString == null) {
            Dialect dialect = ((SessionFactoryImplementor) this.getSessionFactory()).getDialect();
            String guidStr = dialect.getSelectGUIDString();
            guidStr = guidStr.replace("select ", "").replace(" from dual", "");
            this.selectGUIDString = guidStr;
        }
        return this.selectGUIDString;
    }

    protected SQLQuery prepareSQLQuery(Session session,String query,Object[] parameterValues) {
    	if(parameterValues == null || parameterValues.length == 0){
    		return session.createSQLQuery(query); 
    	}
    	return prepareSQLQuery(session,new StringBuilder(query),parameterValues);
    }
    
    protected SQLQuery prepareSQLQuery(Session session,StringBuilder query,Object[] parameterValues) {
    	if(parameterValues != null && parameterValues.length > 0){
    		List<Object> list = new ArrayList<>();
    		QueryUtils.convertComplexQueryParameter(query,parameterValues,list);
    		SQLQuery queryObject = session.createSQLQuery(query.toString());
    		Object val;Type type;
    		if(list.isEmpty()){
        	    for(int i = 0;i < parameterValues.length;i ++){
                	val = parameterValues[i];
                	type = HibernateUtils.getParameterType(val);
                	queryObject.setParameter(i, val, type);
                }
    		}else {
        	    for(int i = 0;i < list.size();i ++){
                	val = list.get(i);
                	type = HibernateUtils.getParameterType(val);
                	queryObject.setParameter(i, val, type);
                }    			
    		}
    		return queryObject;
    	}
    	return session.createSQLQuery(query.toString());
    }
    
    protected SQLQuery prepareSQLQuery(Session session, String query, Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues,String orderBy) {
    	return this.prepareSQLQuery(session, new StringBuilder(query), parameters, extraSqlCondition, parameterValues, orderBy);
    }
    
    protected SQLQuery prepareSQLQuery(Session session, StringBuilder query, Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues,String orderBy) {
    	//parameters = parameters != null ? new HashMap<String, Object>(parameters) : new HashMap<String, Object>();
    	if(!StringUtils.isEmpty(extraSqlCondition)){
    		StringBuilder condition = new StringBuilder(extraSqlCondition);
    		HibernateUtils.toNamedParameters(session.getSessionFactory(), condition);
    		QueryUtils.addExtraConditions(query,condition.toString());
    		HibernateUtils.arrayParamsToMap(parameters, parameterValues);
    	}
    	QueryUtils.convertComplexNamedQueryParameter(query, parameters);
    	QueryUtils.addOrderBy(query,orderBy);
    	AbstractQueryImpl queryObject = (AbstractQueryImpl)session.createSQLQuery(query.toString());
    	for(Entry<String,Object> entry : parameters.entrySet()) {
    		if(StringUtils.isEmpty(entry.getValue()) || 
    				!queryObject.getParameterMetadata().getNamedParameterNames().contains(entry.getKey())){
    			continue;
    		}
    		queryObject.setParameter(entry.getKey(), entry.getValue(), HibernateUtils.getParameterType(entry.getValue()));
    	}
    	return (SQLQuery)queryObject;
    }    
    
    @Override
    public void afterPropertiesSet() throws Exception {
        if (this.factoryProvider == null) {
            Collection<SessionFactory> factories = AbstractContext.getContext().getBeansOfType(SessionFactory.class).values();
            if (factories.size() == 1) {
                this.setSessionFactory(factories.iterator().next());
            } else {
                this.factoryProvider = HibernateSessionFactoryProvider.INSTANCE;
            }
        }
    }

	public void setFactoryProvider(SessionFactoryProvider factoryProvider) {
		this.factoryProvider = factoryProvider;
	}

}
