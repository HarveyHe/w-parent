package com.harvey.w.core.dao.query;

import java.util.Map;

import org.hibernate.SessionFactory;

public interface RowCountQueryCache {

	int queryRowCount(SessionFactory sessionFactory, String queryName,String sql,Map<String, Object> parameters,String extraSqlCondition, Object[] parameterValues,RowCountQueryRunner runner);
}
