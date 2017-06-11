package com.harvey.w.core.dao.utils;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.engine.query.spi.ParameterMetadata;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.persister.entity.AbstractEntityPersister;
import org.hibernate.tuple.entity.EntityTuplizer;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.Type;

import com.harvey.w.core.model.PagingInfo;
import com.harvey.w.core.utils.BeanUtils;

public class HibernateUtils {
	
    @SuppressWarnings("deprecation")
    public static TreeMap<Integer, String> getSqlParameters(SessionFactory sessionFactory, String sql) {
        ParameterMetadata parameterMetaData = ((SessionFactoryImplementor) sessionFactory).getQueryPlanCache().getSQLParameterMetadata(sql);
        Set<String> parameterNames = parameterMetaData.getNamedParameterNames();

        TreeMap<Integer, String> parameters = new TreeMap<Integer, String>();
        for (String parameterName : parameterNames) {
            int[] locations = parameterMetaData.getNamedParameterSourceLocations(parameterName);
            for (int location : locations) {
                parameters.put(location, parameterName);
            }
        }
        return parameters;
    }	
	
	public static Number getSysVersion(Serializable id, SessionFactory sessionFactory, Class<?> entityClass) {
		AbstractEntityPersister metaData = (AbstractEntityPersister) sessionFactory.getClassMetadata(entityClass);
		String sql = "SELECT sys_version FROM " + metaData.getTableName() + " WHERE "
		        + metaData.getIdentifierColumnNames()[0] + "=?";
		Session session = sessionFactory.getCurrentSession();
		Query query = session.createSQLQuery(sql).setParameter(0, id);
		Object result = query.uniqueResult();
		if (result instanceof Number) {
			return (Number) result;
		}
		return null;
	}

	public static String getIdFieldName(SessionFactory sessionFactory, Class<?> entityClass) {
		AbstractEntityPersister metaData = (AbstractEntityPersister) sessionFactory.getClassMetadata(entityClass);
		return metaData.getIdentifierColumnNames()[0];
	}

	public static String getTableName(SessionFactory sessionFactory, Class<?> entityClass) {
		AbstractEntityPersister metaData = (AbstractEntityPersister) sessionFactory.getClassMetadata(entityClass);
		return metaData.getTableName();
	}

	public static void setId(SessionFactory sessionFactory, Object entity, Serializable id, Class<?> entityClass) {
		if (entityClass == null) {
			entityClass = EntityUtils.getEntityClass(entity.getClass());
		}
		sessionFactory.getClassMetadata(entityClass).setIdentifier(entity, id,
		        (SessionImplementor) sessionFactory.getCurrentSession());
	}

	public static Serializable getId(SessionFactory sessionFactory, Object entity, Class<?> entityClass) {
		if (entityClass == null) {
			entityClass = EntityUtils.getEntityClass(entity.getClass());
		}
		AbstractEntityPersister cm = (AbstractEntityPersister) sessionFactory.getClassMetadata(entityClass);
		SessionImplementor session = (SessionImplementor) sessionFactory.getCurrentSession();
		Serializable id = cm.getIdentifier(entity, session);
		if (cm.getIdentifierColumnNames().length > 1 && entity.equals(id)) {// 多主键
			EntityTuplizer et = cm.getEntityTuplizer();
			Object idEntity = et.instantiate();
			cm.getEntityTuplizer().setIdentifier(idEntity, id, session);
			return (Serializable) idEntity;
		}
		return id;
	}

	public static void setPagingInfo(Criteria criteria, PagingInfo pagingInfo) {
		if (pagingInfo != null) {
			if (pagingInfo.getPageSize() <= 0) {
				pagingInfo.setPageSize(10);
			}
			// if (pagingInfo.getPageNo() > pagingInfo.getTotalPages()) {
			// pagingInfo.setPageNo(pagingInfo.getTotalPages());
			// }
			if (pagingInfo.getPageNo() <= 0) {
				pagingInfo.setPageNo(1);
			}
			criteria.setFirstResult(pagingInfo.getCurrentRow());
			criteria.setMaxResults(pagingInfo.getPageSize());
		}
	}

	public static void setPagingInfo(Query query, PagingInfo pagingInfo) {
		if (pagingInfo != null) {
			if (pagingInfo.getPageSize() <= 0) {
				pagingInfo.setPageSize(10);
			}
			// if (pagingInfo.getPageNo() > pagingInfo.getTotalPages()) {
			// pagingInfo.setPageNo(pagingInfo.getTotalPages());
			// }
			if (pagingInfo.getPageNo() <= 0) {
				pagingInfo.setPageNo(1);
			}
			query.setFirstResult(pagingInfo.getCurrentRow());
			query.setMaxResults(pagingInfo.getPageSize());
		}
	}

	public static void addSqlCondtion(SessionFactory sessionFactory, Criteria criteria, String sqlCondition,
	        Map<String, Object> parameter) {
		if (sqlCondition == null || sqlCondition.trim().length() == 0) {
			return;
		} else if (parameter == null || parameter.isEmpty()) {
			criteria.add(Restrictions.sqlRestriction(sqlCondition));
			return;
		}

	}

	public static void addSqlCondtion(SessionFactory sessionFactory, Criteria criteria, String sqlCondition,
	        Object[] parameterValues) {
		if (sqlCondition == null || sqlCondition.trim().length() == 0) {
			return;
		} else if (parameterValues == null || parameterValues.length == 0) {
			criteria.add(Restrictions.sqlRestriction(sqlCondition));
		} else {
			boolean hasArrayParameters = false;
			for (Object value : parameterValues) {
				if (value != null && value.getClass().isArray() || value instanceof Collection) {
					hasArrayParameters = true;
					break;
				}
			}
			if (hasArrayParameters) {
				int[] parameterLocations = getOrdinalParameterLocations(sessionFactory, sqlCondition);
				StringBuilder sqlConditionSb = new StringBuilder(sqlCondition);
				List<Object> parameterValuesList = new ArrayList<Object>();
				for (int i = parameterValues.length - 1; i >= 0; i--) {
					Object value = parameterValues[i];
					if (value instanceof Collection) {
						value = ((Collection<?>) value).toArray();
					}
					if (value != null && value.getClass().isArray() && Array.getLength(value) == 0) {
						value = null;
					}
					if (value != null && value.getClass().isArray()) {
						int size = Array.getLength(value);
						if (size > 1) {
							sqlConditionSb.insert(parameterLocations[i], StringUtils.repeat("?, ", size - 1));
						}
						for (int j = size - 1; j >= 0; j--) {
							parameterValuesList.add(0, Array.get(value, j));
						}
					} else {
						parameterValuesList.add(0, value);
					}
				}
				parameterValues = parameterValuesList.toArray();
				sqlCondition = sqlConditionSb.toString();
			}

			Type[] parameterTypes = getParameterTypes(parameterValues);
			criteria.add(Restrictions.sqlRestriction(sqlCondition, parameterValues, parameterTypes));
		}
	}

	@SuppressWarnings("deprecation")
	public static int[] getOrdinalParameterLocations(SessionFactory sessionFactory, String sql) {
		ParameterMetadata parameterMetaData = ((SessionFactoryImplementor) sessionFactory).getQueryPlanCache()
		        .getSQLParameterMetadata(sql);
		int count = parameterMetaData.getOrdinalParameterCount();
		int[] ordinalParameterLocations = new int[count];
		for (int i = 0; i < ordinalParameterLocations.length; i++) {
			ordinalParameterLocations[i] = parameterMetaData.getOrdinalParameterSourceLocation(i + 1);
		}
		return ordinalParameterLocations;
	}

	public static Type getParameterType(Object value) {
		if (value instanceof String) {
			return StandardBasicTypes.STRING;
		} else if (value instanceof BigDecimal) {
			return StandardBasicTypes.BIG_DECIMAL;
		} else if (value instanceof Double) {
			return StandardBasicTypes.DOUBLE;
		} else if (value instanceof Integer) {
			return StandardBasicTypes.INTEGER;
		} else if (value instanceof Long) {
			return StandardBasicTypes.LONG;
		} else if (value instanceof Date) {
			return StandardBasicTypes.TIMESTAMP;
		} else if(value != null && BeanUtils.isSimpleValueType(value.getClass())){
			return StandardBasicTypes.STRING;			
		} else {
			return StandardBasicTypes.SERIALIZABLE;
		}
	}

	public static Type[] getParameterTypes(Object[] parameterValues) {
		Type[] parameterTypes = new Type[parameterValues.length];
		for (int i = 0; i < parameterValues.length; i++) {
			Object value = parameterValues[i];
			parameterTypes[i] = getParameterType(value);
		}
		return parameterTypes;
	}

	public static List<Order> parseOrderByToHibernateOrders(String orderBy) {
		List<Order> result = new ArrayList<Order>();
		String[] array = org.springframework.util.StringUtils.tokenizeToStringArray(orderBy, " ,`\"[]");
		if (array != null) {
			int nTemp;
			String sTemp, fieldName;
			for (int i = 0; i < array.length; i++) {
				sTemp = array[i];
				if (sTemp.equalsIgnoreCase("delete") || sTemp.equalsIgnoreCase("select")
				        || sTemp.equalsIgnoreCase("insert") || sTemp.equalsIgnoreCase("asc")
				        || sTemp.equalsIgnoreCase("desc")) {
					continue;
				}
				fieldName = EntityUtils.toPascalCase(sTemp, false);
				nTemp = i + 1;
				if (nTemp < array.length) {
					sTemp = array[nTemp];
					if (sTemp.equalsIgnoreCase("desc")) {
						result.add(Order.desc(fieldName));
						i = nTemp;
						continue;
					}
				}
				result.add(Order.asc(fieldName));
			}
		}
		return result;
	}

	public static void addOrderBy(Criteria criteria, String orderBy) {
		if (orderBy != null && orderBy.trim().length() != 0) {
			List<Order> orders = parseOrderByToHibernateOrders(orderBy);
			for (Order order : orders) {
				criteria.addOrder(order);
			}
		}
	}

	public static String[] getFieldNames(SessionFactory factory, Class<?> entityClass) {
		ClassMetadata cm = factory.getClassMetadata(entityClass);
		String[] fieldNames = cm.getPropertyNames();
		fieldNames = Arrays.copyOf(fieldNames, fieldNames.length + 1);
		fieldNames[fieldNames.length - 1] = cm.getIdentifierPropertyName();
		return fieldNames;
	}

	public static void addExample(SessionFactory factory, Criteria criteria, Object exampleEntity, Class<?> entityClass) {
		Map<String, Object> beanMap = BeanUtils.toMap(exampleEntity);
		String[] fieldNames = getFieldNames(factory, entityClass);
		for (String fieldName : fieldNames) {
			Object value = beanMap.get(fieldName);
			if (EntityUtils.isParamValid(value)) {
				criteria.add(Restrictions.eq(fieldName, value));
			}
		}
	}

	public static StringBuilder toNamedParameters(SessionFactory sessionFactory, StringBuilder sql) {
		if (sql == null || sql.length() == 0) {
			return sql;
		}
		int[] ordinalParameterLocations = getOrdinalParameterLocations(sessionFactory, sql.toString());
		for (int i = ordinalParameterLocations.length - 1; i >= 0; i--) {
			int location = ordinalParameterLocations[i];
			sql.delete(location, location + 1);
			sql.insert(location, ":");
			sql.insert(location + 1, QueryUtils.NAMED_PARAMETER_PREFIX);
			sql.insert(location + 1 + QueryUtils.NAMED_PARAMETER_PREFIX.length(), i);
		}
		return sql;
	}

	public static void arrayParamsToMap(Map<String, Object> namedParameters, Object[] parameterValues) {
		if (parameterValues == null || parameterValues.length == 0)
			return;
		for (int i = 0; i < parameterValues.length; i++) {
			Object value = parameterValues[i];
			if (value == null) {
				value = "";
			}
			if (value.getClass().isArray()) {
				List<Object> notNullArrayItems = new ArrayList<Object>();
				for (int j = 0; j < Array.getLength(value); j++) {
					Object itemValue = Array.get(value, j);
					if (itemValue != null) {
						notNullArrayItems.add(itemValue);
					}
				}
				value = notNullArrayItems.toArray();
				if (Array.getLength(value) == 0) {
					value = "";
				}
			}
			namedParameters.put(QueryUtils.NAMED_PARAMETER_PREFIX + i, value);
		}
	}
}
