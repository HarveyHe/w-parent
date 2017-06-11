package com.harvey.w.core.dao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.harvey.w.core.dao.query.RowCountQueryCache;
import com.harvey.w.core.dao.query.RowCountQueryRunner;
import com.harvey.w.core.dao.utils.HibernateUtils;
import com.harvey.w.core.hibernate.DynamicModelClassResultTransformer;
import com.harvey.w.core.model.BaseModel;
import com.harvey.w.core.model.ModelState;
import com.harvey.w.core.model.UserBaseModel;
import org.hibernate.Criteria;
import org.hibernate.LockOptions;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.StaleObjectStateException;
import org.hibernate.criterion.Projections;
import org.hibernate.engine.spi.SessionFactoryImplementor;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.persister.entity.EntityPersister;
import org.springframework.util.StringUtils;

import com.harvey.w.core.bean.DefaultBeanWrapper;
import com.harvey.w.core.context.AbstractContext;
import com.harvey.w.core.dao.utils.EntityUtils;
import com.harvey.w.core.dao.utils.QueryUtils;
import com.harvey.w.core.exception.ConVersionException;
import com.harvey.w.core.model.DynamicModelClass;
import com.harvey.w.core.model.PagingInfo;
import com.harvey.w.core.reflectasm.MethodAccess;
import com.harvey.w.core.utils.BeanUtils;

public class DefaultUniversalDao extends AbstractHibernateDao implements UniversalDao, RowCountQueryRunner {

	private RowCountQueryCache rowCountQueryCache;

	@Override
	public <MODEL> MODEL get(Class<MODEL> modelClass, Serializable id) {
		if (id == null) {
			return null;
		}
		Class<?> rootModelClass = EntityUtils.getEntityClass(modelClass);
		Object entity = this.getSessionFactory().getCurrentSession().get(rootModelClass, id);
		if (entity != null) {
			evict(entity);
		}
		return EntityUtils.convertEntityType(entity, modelClass);
	}

	@Override
	public <MODEL> MODEL getForUpdate(Class<MODEL> modelClass, Serializable id) {
		if (id == null) {
			return null;
		}
		Class<?> rootModelClass = EntityUtils.getEntityClass(modelClass);
		Object entity = this.getSessionFactory().getCurrentSession().get(rootModelClass, id, LockOptions.UPGRADE);
		if (entity != null) {
			evict(entity);
		}
		return EntityUtils.convertEntityType(entity, modelClass);
	}

	@Override
	public <MODEL> boolean exists(Class<MODEL> modelClass, Serializable id) {
		if (id == null) {
			return false;
		}
		Class<?> rootModelClass = EntityUtils.getEntityClass(modelClass);
		Object entity = this.getSessionFactory().getCurrentSession().get(rootModelClass, id);
		return entity != null;
	}

	@Override
	public <MODEL> List<MODEL> getAll(Class<MODEL> modelClass) {
		return this.getAll(modelClass, null, null);
	}

	@Override
	public <MODEL> List<MODEL> getAll(Class<MODEL> modelClass, String orderBy) {
		return this.getAll(modelClass, orderBy, null);
	}

	@Override
	public <MODEL> List<MODEL> getAll(Class<MODEL> modelClass, String orderBy, PagingInfo pagingInfo) {
		Criteria criteria = this.getSessionFactory().getCurrentSession()
		        .createCriteria(EntityUtils.getEntityClass(modelClass));
		if (pagingInfo != null) {
			criteria.setProjection(Projections.rowCount());
			Object ret = criteria.uniqueResult();
			if (ret != null) {
				pagingInfo.setTotalRows(((Long) ret).intValue());
			} else {
				pagingInfo.setTotalRows(0);
			}
			criteria.setProjection(null);
			HibernateUtils.setPagingInfo(criteria, pagingInfo);
		}
		HibernateUtils.addOrderBy(criteria, orderBy);
		List<?> queryResult = criteria.list();
		List<MODEL> result = new ArrayList<MODEL>();
		for (Object entity : queryResult) {
			evict(entity);
			result.add(EntityUtils.convertEntityType(entity, modelClass));
		}
		return result;
	}

	@Override
	public <MODEL> int getRowCount(Class<MODEL> modelClass) {
		Criteria criteria = this.createCriteria(EntityUtils.getEntityClass(modelClass));
		criteria.setProjection(Projections.rowCount());
		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public <MODEL> List<MODEL> findBySqlCondition(Class<MODEL> modelClass, String sqlCondition, Object[] parameterValues) {
		return this.findBySqlCondition(modelClass, sqlCondition, parameterValues, null, null);
	}

	@Override
	public <MODEL> List<MODEL> findBySqlCondition(Class<MODEL> modelClass, String sqlCondition,
	        Object[] parameterValues, String orderBy) {
		return this.findBySqlCondition(modelClass, sqlCondition, parameterValues, orderBy, null);
	}

	@Override
	public <MODEL> List<MODEL> findBySqlCondition(Class<MODEL> modelClass, String sqlCondition,
	        Object[] parameterValues, String orderBy, PagingInfo pagingInfo) {
		Criteria criteria = this.createCriteria(EntityUtils.getEntityClass(modelClass));
		SessionFactory sessionFactory = this.getSessionFactory();
		HibernateUtils.addSqlCondtion(sessionFactory, criteria, sqlCondition, parameterValues);
		if (pagingInfo != null) {
			criteria.setProjection(Projections.rowCount());
			pagingInfo.setTotalRows(((Long) criteria.uniqueResult()).intValue());
			criteria.setProjection(null);
			HibernateUtils.setPagingInfo(criteria, pagingInfo);
		}
		HibernateUtils.addOrderBy(criteria, orderBy);
		List<?> queryResult = criteria.list();
		List<MODEL> result = new ArrayList<MODEL>();
		Session session = sessionFactory.getCurrentSession();
		for (Object entity : queryResult) {
			session.evict(entity);
			result.add(EntityUtils.convertEntityType(entity, modelClass));
		}
		return result;
	}

	@Override
	public <MODEL> int getRowCountBySqlCondition(Class<MODEL> modelClass, String sqlCondition, Object[] parameterValues) {
		Criteria criteria = this.createCriteria(EntityUtils.getEntityClass(modelClass));
		HibernateUtils.addSqlCondtion(this.getSessionFactory(), criteria, sqlCondition, parameterValues);
		criteria.setProjection(Projections.rowCount());
		return ((Long) criteria.uniqueResult()).intValue();
	}

	@Override
	public <MODEL> List<MODEL> findByExample(MODEL example) {
		return this.findByExample(example, null, null);
	}

	@Override
	public <MODEL> List<MODEL> findByExample(MODEL example, String orderBy) {
		return this.findByExample(example, orderBy, null);
	}

	@Override
	public <MODEL> List<MODEL> findByExample(MODEL example, String orderBy, PagingInfo pagingInfo) {
		return this.findByExample(example, null, null, orderBy, pagingInfo);
	}

	@Override
	public <MODEL> List<MODEL> findByExample(MODEL example, String sqlCondition, Object[] parameterValues,
	        String orderBy, PagingInfo pagingInfo) {
		SessionFactory sessionFactory = getSessionFactory();
		Session session = sessionFactory.getCurrentSession();
		Class<?> entityClass = EntityUtils.getEntityClass(example.getClass());
		String entityName = entityClass.getName();
		Criteria criteria = entityName != null ? session.createCriteria(entityName) : sessionFactory
		        .getCurrentSession().createCriteria(entityClass);
		HibernateUtils.addExample(sessionFactory, criteria, example, entityClass);
		HibernateUtils.addSqlCondtion(sessionFactory, criteria, sqlCondition, parameterValues);
		if (pagingInfo != null) {
			criteria.setProjection(Projections.rowCount());
			pagingInfo.setTotalRows(((Long) criteria.uniqueResult()).intValue());
			criteria.setProjection(null);
			HibernateUtils.setPagingInfo(criteria, pagingInfo);
		}
		HibernateUtils.addOrderBy(criteria, orderBy);
		List<?> queryResult = criteria.list();
		List<MODEL> result = new ArrayList<MODEL>();
		for (Object entity : queryResult) {
			session.evict(entity);
			result.add(EntityUtils.convertEntityType(entity, (Class<MODEL>) example.getClass()));
		}
		return result;
	}

	@Override
	public <MODEL> int getRowCountByExample(MODEL example) {
		return this.getRowCountByExample(example, null, null);
	}

	@Override
	public <MODEL> int getRowCountByExample(MODEL example, String sqlCondition, Object[] parameterValues) {
		SessionFactory sessionFactory = getSessionFactory();
		Class<?> entityClass = EntityUtils.getEntityClass(example.getClass());
		String entityName = entityClass.getName();
		Criteria criteria = entityName != null ? sessionFactory.getCurrentSession().createCriteria(entityName)
		        : sessionFactory.getCurrentSession().createCriteria(entityClass);
		HibernateUtils.addExample(sessionFactory, criteria, example, entityClass);
		HibernateUtils.addSqlCondtion(sessionFactory, criteria, sqlCondition, parameterValues);
		criteria.setProjection(Projections.rowCount());
		return ((Long) criteria.uniqueResult()).intValue();
	}

	private <MODEL> MODEL saveNormalModel(MODEL model) {
		Class<?> entityClass = EntityUtils.getEntityClass(model.getClass());
		String entityName = entityClass.getName();
		Serializable id = EntityUtils.getId(model);
		SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) this.getSessionFactory();
		doSystemFields(sessionFactory, model, id, entityClass);
		Object entity = sessionFactory.getCurrentSession().save(entityName, model);
		if (model.getClass() == entity.getClass()) {
			return (MODEL) entity;
		} else {
			// BeanUtils.copyProperties(entity, model);
			BeanUtils.copy(entity, model);
			return model;
		}
	}

	/**
	 * Concurrent Version Control and common fields
	 * 
	 * @param model
	 * @param persistantModel
	 * @param entityClass
	 */
	private void doSystemFields(SessionFactoryImplementor sessionFactory, Object model, Serializable id,
	        Class<?> entityClass) {
		UserBaseModel user = AbstractContext.getCurrentUser();
		Serializable userId = user != null ? user.primeryKeyValue() : null;
		MethodAccess access = MethodAccess.get(entityClass);
		Date date = null;
		if (access.hasMethod("getCreateTime") && access.tryInvoke(model, "getCreateTime") == null) {
			date = getSysDate();
			access.tryInvoke(model, "setCreateTime", date);
			access.tryInvoke(model, "setCreator", userId);
		}
		if (access.hasMethod("setModifyTime")) {
			if (date == null)
				date = getSysDate();
			access.tryInvoke(model, "setModifyTime", date);
			access.tryInvoke(model, "setModifier", userId);
		}
		// Concurrent Version Control
		if (!sessionFactory.getEntityPersister(entityClass.getName()).isVersioned()
		        && access.hasMethod("getSysVersion") && access.hasMethod("setSysVersion")) {
			Number ver;
			if (id == null) {
				ver = 1;
			} else {
				ver = (Number) access.tryInvoke(model, "getSysVersion");
				Number sysVer = EntityUtils.getSysVersion(id, getSessionFactory(), entityClass);
				if (sysVer != null && !sysVer.equals(ver)) {
					throw new ConVersionException(id, sysVer, ver);
				}
				ver = ver == null ? 1 : ver.intValue() + 1;
			}
			access.invoke(model, "setSysVersion", ver);
		}
	}

	@Override
	public <MODEL> MODEL save(MODEL model) {
		SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) this.getSessionFactory();
		try {
			BaseModel baseModel = null;
			if (model instanceof BaseModel) {
				baseModel = (BaseModel) model;
			} else {
				return saveNormalModel(model);
			}
			if (ModelState.Deleted.equals(baseModel.getModelState())) {
				this.remove(model);
				return null;
			} else {
				Class<?> entityClass = EntityUtils.getEntityClass(model.getClass());
				Session session = sessionFactory.getCurrentSession();
				Serializable id = EntityUtils.getId(sessionFactory, baseModel, entityClass);
				if (id != null) {
					Object persistantModel = session.get(entityClass, id);
					if (persistantModel != null) {
						session.evict(persistantModel);
						new DefaultBeanWrapper(baseModel).copyPropertiesTo(persistantModel,
						        Collections.unmodifiableList(baseModel.validFields()));
						if (model.getClass() == persistantModel.getClass()) {
							model = (MODEL) persistantModel;
						} else {
							BeanUtils.copy(persistantModel, model);
						}
					}
				}
				doSystemFields(sessionFactory, model, id, entityClass);
				Object entity = session.merge(entityClass.getName(), model);
				session.flush();
				session.evict(entity);
				if (model.getClass() == entity.getClass()) {
					if (id == null) {
						id = EntityUtils.getId(entity);
						EntityUtils.setId(model, id);
					}
					return (MODEL) entity;
				} else {
					BeanUtils.copy(entity, model);
					return model;
				}
			}
		} catch (StaleObjectStateException sosex) {
			EntityPersister ep = sessionFactory.getEntityPersister(sosex.getEntityName());
			if (ep.isVersioned()) {
				Object ver = ep.getVersion(model);
				throw new ConVersionException(sosex.getIdentifier(), ver);
			}
			throw new RuntimeException("Data has been modified by another user. Please reload and try again.");
		}
	}

	@Override
	public <MODEL> Collection<MODEL> saveAll(Collection<MODEL> models) {
		if (models.isEmpty()) {
			return models;
		}
		SessionFactoryImplementor sessionFactory = (SessionFactoryImplementor) getSessionFactory();
		try {
			List<MODEL> modelsToDelete = new ArrayList<MODEL>();
			List<MODEL> modelsToMerge = new ArrayList<MODEL>();
			for (MODEL model : models) {
				if (!(model instanceof BaseModel)) {
					continue;
				}
				BaseModel baseModel = (BaseModel) model;
				if (ModelState.Deleted.equals(baseModel.getModelState())) {
					modelsToDelete.add(model);
				} else {
					modelsToMerge.add(model);
				}
			}
			this.removeAll(modelsToDelete);
			if (modelsToMerge.size() == 0) {
				return Collections.emptyList();
			} else {
				Class<?> entityClass = EntityUtils.getEntityClass(modelsToMerge.iterator().next().getClass());
				String entityName = entityClass.getName();
				List<Object> mergeResult = new ArrayList<Object>();
				Session session = sessionFactory.getCurrentSession();
				for (MODEL model : modelsToMerge) {
					Serializable id = EntityUtils.getId(sessionFactory, model, entityClass);
					BaseModel baseModel = (BaseModel) model;
					if (id != null) {
						Object persistantModel = session.get(entityClass, id);
						if (persistantModel != null) {
							session.evict(persistantModel);
							new DefaultBeanWrapper(model).copyPropertiesTo(persistantModel,
							        Collections.unmodifiableList(baseModel.validFields()));
							if (baseModel.getClass() == persistantModel.getClass()) {
								baseModel = (BaseModel) persistantModel;
							} else {
								BeanUtils.copy(persistantModel, baseModel);
							}
						}
					}
					doSystemFields(sessionFactory, baseModel, id, entityClass);
					mergeResult.add(session.merge(entityName, baseModel));
				}
				session.flush();
				List<MODEL> result = new ArrayList<MODEL>();
				for (int i = 0; i < modelsToMerge.size(); i++) {
					MODEL model = modelsToMerge.get(i);
					Object entity = mergeResult.get(i);
					evict(entity);
					if (model.getClass() == entity.getClass()) {
						Serializable id = EntityUtils.getId(sessionFactory, entity, entityClass);
						EntityUtils.setId(sessionFactory, model, id, entityClass);
						result.add((MODEL) entity);
					} else {
						BeanUtils.copy(entity, model);
						result.add(model);
					}
				}
				return result;
			}
		} catch (StaleObjectStateException sosex) {
			EntityPersister ep = sessionFactory.getEntityPersister(sosex.getEntityName());
			if (ep.isVersioned()) {
				Object ver = null;
				SessionImplementor session = (SessionImplementor) sessionFactory.getCurrentSession();
				if (sosex.getIdentifier() != null) {
					for (Object model : models) {
						if (sosex.getIdentifier().equals(ep.getIdentifier(model, session))) {
							ver = ep.getVersion(model);
							break;
						}
					}
				}
				throw new ConVersionException(sosex.getIdentifier(), ver);
			}
			throw new RuntimeException("Data has been modified by another user. Please reload and try again.");
		}
	}

	@Override
	public <MODEL> void remove(MODEL model) {
		try {
			Session session = this.getSessionFactory().getCurrentSession();
			session.delete(EntityUtils.getEntityClass(model.getClass()).getName(), model);
			session.flush();
		} catch (StaleObjectStateException sosex) {
			throw new RuntimeException("Data has been modified by another user. Please reload and try again.");
		}
	}

	@Override
	public <MODEL> void removeAll(Collection<MODEL> models) {
		try {
			if (models.size() == 0) {
				return;
			} else {
				Session session = this.getSessionFactory().getCurrentSession();
				String entityName = EntityUtils.getEntityClass(models.iterator().next().getClass()).getName();
				for (MODEL model : models) {
					session.delete(entityName, model);
				}
				session.flush();
			}
		} catch (StaleObjectStateException sosex) {
			throw new RuntimeException("Data has been modified by another user. Please reload and try again.");
		}
	}

	@Override
	public <MODEL> void removeByPk(Class<MODEL> modelClass, Serializable id) {
		this.remove(this.get(modelClass, id));
	}

	@Override
	public <MODEL> void removeAllByPk(Class<MODEL> modelClass, Collection<? extends Serializable> ids) {
		List<MODEL> models = new ArrayList<MODEL>();
		for (Serializable id : ids) {
			models.add(this.get(modelClass, id));
		}
		this.removeAll(models);
	}

	@Override
	public <ITEM> List<ITEM> query(Object condition, Class<ITEM> itemClass) {
		return this.query(condition, itemClass, null, null, null, null);
	}

	@Override
	public <ITEM> List<ITEM> query(Object condition, Class<ITEM> itemClass, String orderBy) {
		return this.query(condition, itemClass, orderBy, null);
	}

	@Override
	public <ITEM> List<ITEM> query(Object condition, Class<ITEM> itemClass, PagingInfo pagingInfo) {
		return this.query(condition, itemClass, null, null, null, pagingInfo);
	}

	@Override
	public <ITEM> List<ITEM> query(Object condition, Class<ITEM> itemClass, String orderBy, PagingInfo pagingInfo) {
		return this.query(condition, itemClass, null, null, orderBy, pagingInfo);
	}

	@Override
	public <ITEM> List<ITEM> query(Object condition, Class<ITEM> itemClass, String extraSqlCondition,
	        Object[] parameterValues, String orderBy, PagingInfo pagingInfo) {
		Class<?> resultEntityClass;
		if (!BaseModel.class.isAssignableFrom(itemClass) || BaseModel.class.equals(itemClass)) {
			resultEntityClass = QueryUtils.getQueryItemClass(condition);
		} else {
			resultEntityClass = EntityUtils.getEntityClass(itemClass);
		}
		SessionFactory sessionFactory = getSessionFactory();
		Map<String, Object> parameters = new HashMap<String, Object>(BeanUtils.toMap(condition));
		String queryName = QueryUtils.getSqlQueryName(condition);
		StringBuilder sql = new StringBuilder(QueryUtils.getDynamicSql(sessionFactory, queryName, parameters));
		if (pagingInfo != null) {
			int rowCount = this.queryRowCountInternal(sessionFactory, queryName, sql.toString(), parameters,extraSqlCondition,parameterValues);
			pagingInfo.setTotalRows(rowCount);
		}
		if(sql.indexOf(QueryUtils.SQL_EXTRA_UUID_MACRO) == -1) {
			QueryUtils.insert(sql, 0, "select T__UUID__.*, ", this.getSelectGUIDString(), " as UUID__ from (").append(") T__UUID__");
		}
		Session session = sessionFactory.getCurrentSession();
		SQLQuery queryObject = super.prepareSQLQuery(session, sql, parameters, extraSqlCondition, parameterValues,orderBy);
		queryObject.addEntity(resultEntityClass);
		if (pagingInfo != null) {
			HibernateUtils.setPagingInfo(queryObject, pagingInfo);
		}

		List<Object> queryResult = queryObject.list();
		List<ITEM> result = new ArrayList<ITEM>();
		for (Object entity : queryResult) {
			session.evict(entity);
			result.add(EntityUtils.convertEntityType(entity, itemClass));
		}
		return result;
	}

	@Override
	public <ITEM> List<ITEM> query(String queryName, Class<ITEM> itemClass, Map<String, Object> parameters,
	        String extraSqlCondition, Object[] parameterValues, String orderBy, PagingInfo pagingInfo) {
		itemClass = (Class<ITEM>) EntityUtils.getEntityClass(itemClass);
		parameters = parameters != null ? new HashMap<String, Object>(parameters) : new HashMap<String, Object>();
		SessionFactory sessionFactory = getSessionFactory();
		StringBuilder sql = new StringBuilder(QueryUtils.getDynamicSql(sessionFactory, queryName, parameters));
		if (pagingInfo != null) {
			int rowCount = this.queryRowCountInternal(sessionFactory, queryName, sql.toString(), parameters,extraSqlCondition,parameterValues);
			pagingInfo.setTotalRows(rowCount);
		}
		if(sql.indexOf(QueryUtils.SQL_EXTRA_UUID_MACRO) == -1) {
			QueryUtils.insert(sql, 0, "select T__UUID__.*, ", this.getSelectGUIDString(), " as UUID__ from (").append(") T__UUID__");
		}
		Session session = sessionFactory.getCurrentSession();
		SQLQuery queryObject = super.prepareSQLQuery(session, sql, parameters, extraSqlCondition, parameterValues,orderBy);
		queryObject.addEntity(itemClass);
		if (pagingInfo != null) {
			HibernateUtils.setPagingInfo(queryObject, pagingInfo);
		}
		List<Object> queryResult = queryObject.list();
		List<ITEM> result = new ArrayList<ITEM>();
		for (Object entity : queryResult) {
			session.evict(entity);
			result.add(EntityUtils.convertEntityType(entity, itemClass));
		}
		return result;
	}

	private int queryRowCountInternal(SessionFactory sessionFactory, String queryName, String sql,Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues) {
		if (this.rowCountQueryCache != null) {
			return rowCountQueryCache.queryRowCount(sessionFactory, queryName, sql, parameters,extraSqlCondition,parameterValues, this);
		}
		return this.runRowCountQuery(sessionFactory, queryName, sql, parameters,extraSqlCondition,parameterValues);
	}

	@Override
	public int runRowCountQuery(SessionFactory sessionFactory, String queryName, String sql,Map<String, Object> parameters, String extraSqlCondition, Object[] parameterValues) {
		if (!StringUtils.isEmpty(queryName)) {
			String countQueryName = queryName.concat("_Count");
			if (QueryUtils.queryExists(sessionFactory, countQueryName)) {
				String query = QueryUtils.getDynamicSql(sessionFactory, countQueryName, parameters);
				SQLQuery queryObject = this.prepareSQLQuery(sessionFactory.getCurrentSession(), query, parameters,extraSqlCondition, parameterValues, null);
				Number num = BeanUtils.of(queryObject.uniqueResult(), Number.class);
				if (num != null) {
					return num.intValue();
				}
			}
		}
		if(StringUtils.isEmpty(sql)){
			sql = QueryUtils.getDynamicSql(sessionFactory, queryName, parameters);
		}
		return super.queryRowCountBySql(sql, parameters, extraSqlCondition, parameterValues);
	}

	@Override
	public int queryRowCount(Object condition, String extraSqlCondition, Object[] parameterValues) {
		Map<String, Object> parameters = BeanUtils.toMap(condition);
		String queryName = QueryUtils.getSqlQueryName(condition);
		SessionFactory sessionFactory = this.getSessionFactory();
		return this.queryRowCountInternal(sessionFactory, queryName, null, parameters, extraSqlCondition, parameterValues);
	}

	@Override
	public List<DynamicModelClass> query(String queryName, Map<String, Object> parameters) {
		return this.query(queryName, parameters, null, null);
	}

	@Override
	public List<DynamicModelClass> query(String queryName, Map<String, Object> parameters, String orderBy) {
		return this.query(queryName, parameters, orderBy, null);
	}

	@Override
	public List<DynamicModelClass> query(String queryName, Map<String, Object> parameters, PagingInfo pagingInfo) {
		return this.query(queryName, parameters, null, pagingInfo);
	}

	@Override
	public List<DynamicModelClass> query(String queryName, Map<String, Object> parameters, String orderBy,
	        PagingInfo pagingInfo) {
		return this.query(queryName, parameters, null, null, orderBy, pagingInfo);
	}

	@Override
	public List<DynamicModelClass> query(String queryName, Map<String, Object> parameters, String extraSqlCondition,
	        Object[] parameterValues, String orderBy, PagingInfo pagingInfo) {
		SessionFactory sessionFactory = this.getSessionFactory();
		parameters = parameters != null ? new HashMap<String, Object>(parameters) : new HashMap<String, Object>();
		StringBuilder sql = new StringBuilder(QueryUtils.getDynamicSql(sessionFactory, queryName, parameters));
		if (pagingInfo != null) {
			int rowCount = this.queryRowCountInternal(sessionFactory, queryName, sql.toString(), parameters,extraSqlCondition,parameterValues);
			pagingInfo.setTotalRows(rowCount);
		}
		if(sql.indexOf(QueryUtils.SQL_EXTRA_UUID_MACRO) == -1) {
			QueryUtils.insert(sql, 0, "select T__UUID__.*, ", this.getSelectGUIDString(), " as UUID__ from (").append(") T__UUID__");
		}		
		SQLQuery queryObject = super.prepareSQLQuery(sessionFactory.getCurrentSession(), sql, parameters, extraSqlCondition, parameterValues,orderBy);
		queryObject.setResultTransformer(DynamicModelClassResultTransformer.getInstance());
		if (pagingInfo != null) {
			HibernateUtils.setPagingInfo(queryObject, pagingInfo);
		}
		@SuppressWarnings("unchecked")
		List<DynamicModelClass> queryResult = queryObject.list();
		return queryResult;
	}

	@Override
	public int queryRowCount(String queryName, Map<String, Object> parameters, String extraSqlCondition,Object[] parameterValues) {
		return this.queryRowCountInternal(getSessionFactory(), queryName, null, parameters, extraSqlCondition, parameterValues);
	}

	@Override
	public int update(Object condition) {
		SessionFactory sessionFactory = this.getSessionFactory();
		String sql = QueryUtils.getNamedSql(sessionFactory, QueryUtils.getSqlUpdateName(condition));
		SQLQuery queryObject = sessionFactory.getCurrentSession().createSQLQuery(sql);
		queryObject.setProperties(condition);
		return queryObject.executeUpdate();
	}

	@Override
	public int update(String updateName, Map<String, Object> parameters) {
		SessionFactory sessionFactory = this.getSessionFactory();
		String sql = QueryUtils.getNamedSql(sessionFactory, updateName);
		SQLQuery queryObject = sessionFactory.getCurrentSession().createSQLQuery(sql);
		queryObject.setProperties(parameters);
		return queryObject.executeUpdate();
	}

	public RowCountQueryCache getRowCountQueryCache() {
		return rowCountQueryCache;
	}

	public void setRowCountQueryCache(RowCountQueryCache rowCountQueryCache) {
		this.rowCountQueryCache = rowCountQueryCache;
	}

}
