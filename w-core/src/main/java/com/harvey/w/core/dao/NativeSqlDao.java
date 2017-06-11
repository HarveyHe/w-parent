package com.harvey.w.core.dao;

import java.util.List;
import java.util.Map;

import com.harvey.w.core.model.DynamicModelClass;
import com.harvey.w.core.model.PagingInfo;

/**
 * 通用原生sql数据访问接口<
 * 此接口不依赖特定数据持久层框架,可以采用hibernate或mybatis实现sql执行能力..
 * @author dream.chen
 *
 */
public interface NativeSqlDao extends HibernateDao {
    
    /**
     * 执行数据库定义语言<br>
     * 如:create table、drop table等。<br>
     * 如果jdbc驱动支持多行批量执行,则此方法也支持多行语句.
     * @param sql 执行的语句
     */
    void executeDDL(String sql);
    
    /**
     * 执行批量更新
     * 示例:
     * <pre><code>
     * String sql = "insert into sys_user(user_id,user_name)values(?,?)";
     * Object[][] parameter = new Object[][]{{1,"test1"},{2,"test2"}};
     * Integer[] result = batchUpdate(sql,parameter);
     * for(int i = 0;i < result.length;i++){
     *     System.out.println("第"+(i+1)+"次插入了"+result[i]+"行");
     * }
     * <code></pre>
     * @param sql 更新语句,如insert、update、delete
     * @param parameters 二维数组参数
     * @return 返回每次更新的语句影响行数
     */
    Integer[] batchUpdate(String sql,Object[][] parameters);
    
    
    /**
     * 
     * @param sql
     * @param parameters
     * @return
     */
    int bulkUpdate(String sql, Object... parameters);
    
    /**
     * 
     * @param sql
     * @param parameters
     * @return
     */
    int bulkUpdate(String sql, Map<String, Object> parameters);
    
    /**
     * 参数化查询返回单列单行结果
     * @param sql 查询语句
     * @param args 查询参数
     * @return 首行首列的结果
     */
    <TResult> TResult queryScalar(String sql, Object... args);

    /**
     * 命名参数查询返回单列单行结果
     * @param sql 查询语句
     * @param args 查询参数
     * @return 首行首列的结果
     */
    <TResult> TResult queryScalar(String sql, Map<String, Object> args);
    
    <TResult> List<TResult> queryScalarList(String sql, Object... parameters);
    
    <TResult> List<TResult> queryScalarList(String sql, Map<String, Object> parameters);
    
    <TResult> List<TResult> queryList(Class<TResult> model,String sql, Object... parameters);
    
    <TResult> List<TResult> queryList(Class<TResult> model,String sql, Map<String, Object> parameters);
    
    List<DynamicModelClass> query(String sql, Map<String, Object> parameters);

    List<DynamicModelClass> query(String sql, Map<String, Object> parameters, PagingInfo pagingInfo);
    
    List<DynamicModelClass> query(String sql, Object... parameters);

    List<DynamicModelClass> query(String sql, Object[] parameters, PagingInfo pagingInfo);
    
    /**
     * 支持的格式为: sp_test(uid IN int,count OUT int)
     */    
    Map<String,Object> executeStoredProcedure(String spName,Map<String,Object> parameters);
}
