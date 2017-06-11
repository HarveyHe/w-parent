package com.harvey.w.core.dao.query;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.harvey.w.core.cache.utils.CacheUtils;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.cache.Cache;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import com.harvey.w.core.utils.BeanUtils;
import com.harvey.w.core.utils.JSON;

public class RowCountQuerySpringCache implements RowCountQueryCache,InitializingBean {

	/**
	 * 超过行数限制才缓存
	 */
	private int rowCountLimit = 500;
	private Map<String,Object> cacheProps = new HashMap<>();
	private Cache cache;
	
	@Override
    public int queryRowCount(SessionFactory sessionFactory, String queryName, String sql,Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues,RowCountQueryRunner runner) {
	    String cacheKey = this.getCacheKey(queryName, sql, parameters, extraSqlCondition, parameterValues);
	    Integer rowCount = this.cache.get(cacheKey, Integer.class);
	    if(rowCount != null) {
	    	return rowCount;
	    }
	    rowCount = runner.runRowCountQuery(sessionFactory, queryName, sql, parameters, extraSqlCondition, parameterValues);
	    if(rowCount >= rowCountLimit) {
	    	this.cache.put(cacheKey, rowCount);
	    }
	    return rowCount;
    }
	
	private String getCacheKey(String queryName, String sql,Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues) {
		StringBuilder builder = new StringBuilder();
		if(!StringUtils.isEmpty(queryName)){
			builder.append(queryName).append('&');
		}else{
			builder.append(sql).append('&');
		}
		if(!CollectionUtils.isEmpty(parameters)){
			for(Entry<String,Object> entry : parameters.entrySet()) {
				if(entry.getValue() != null && BeanUtils.isSimpleValueType(entry.getValue().getClass())) {
					builder.append(entry.getKey()).append('=').append(String.valueOf(entry.getValue())).append('&');
				}
			}
		}
		if(!StringUtils.isEmpty(extraSqlCondition)) {
			builder.append(extraSqlCondition).append('&');
			if(parameterValues != null && parameterValues.length > 0) {
				builder.append(JSON.serialize(parameterValues));
			}
		}
		return builder.toString();
	}
	
	@Override
    public void afterPropertiesSet() throws Exception {
	    if(this.cache == null) {
	    	this.cache = CacheUtils.getCache("RowCountQuerySpringCache", cacheProps);
	    }
    }

	public void setCache(Cache cache) {
		this.cache = cache;
	}

	public void setCacheProperty(Map<String, Object> cacheProperty) {
		this.cacheProps.putAll(cacheProperty);
	}

	public void setTimeToLiveSecond(Long timeToLiveSecond) {
		this.cacheProps.put("timeToLive", timeToLiveSecond);
	}
	
	public void setTimeToIdleSecond(Long timeToIdleSecond) {
		this.cacheProps.put("timeToIdle", timeToIdleSecond);
	}
	
	public void setMaxEntriesInMemory(Integer maxEntriesInMemory) {
		this.cacheProps.put("maxEntriesInMemory", maxEntriesInMemory);
	}

	public void setRowCountLimit(int rowCountLimit) {
		this.rowCountLimit = rowCountLimit;
	}
}
