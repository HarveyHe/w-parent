package com.harvey.w.core.dao.utils;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;

import com.harvey.w.core.context.Context;
import com.harvey.w.core.dao.query.QueryTemplate;
import com.harvey.w.core.model.BaseModel;
import org.beetl.core.Template;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.engine.query.spi.ParamLocationRecognizer;
import org.hibernate.engine.query.spi.ParamLocationRecognizer.NamedParameterDescription;
import org.hibernate.engine.query.spi.ParameterParser;
import org.hibernate.engine.spi.NamedSQLQueryDefinition;
import org.hibernate.internal.SessionFactoryImpl;
import org.springframework.util.StringUtils;

import com.harvey.w.core.utils.BeanUtils;

public class QueryUtils {
    public static final String NAMED_PARAMETER_PREFIX = "$param$_";
    public static final String SQL_EXTRA_CONDITIONS_MACRO = "##CONDITIONS##";
    public static final String SQL_EXTRA_ORDER_MACRO = "##ORDER##";
    public static final String SQL_EXTRA_UUID_MACRO = "UUID__";
    private static final String NAMED_PARAMETER_PREFIX_INNER = "$p_";
    private static Set<Integer> SQL_SEPARATORSET;
    static {
        SQL_SEPARATORSET = new HashSet<Integer>();
        for (char c : "     \n\r\f\t,:()=<>$&|+-=/*'^![]#~\\".toCharArray()) {
            SQL_SEPARATORSET.add((int) c);
        }
    }

    public static String getSqlQueryName(Class<?> conditionClass) {
        String queryName = EntityUtils.getNormalName(conditionClass.getSimpleName());
        int index = queryName.indexOf("QueryCondition_$$_");
        if (index > -1) {
            queryName = queryName.substring(0, index);
        } else if (queryName.endsWith("QueryCondition")) {
            queryName = queryName.substring(0, queryName.length() - 9);
        }
        return queryName;
    }

    public static String getSqlQueryName(Object condition) {
        return getSqlQueryName(condition.getClass());
    }

    public static Class<?> getQueryItemClass(Object condition) {
        String queryConditionClassName = EntityUtils.getNormalName(condition.getClass().getName());
        if (queryConditionClassName.endsWith("QueryCondition")) {
            String queryItemClassName = queryConditionClassName.substring(0, queryConditionClassName.length() - 9) + "Item";
            try {
                return (Class<?>) Class.forName(queryItemClassName);
            } catch (ClassNotFoundException cnfe) {
                throw new RuntimeException(cnfe);
            }
        } else {
            throw new RuntimeException("Item class for " + queryConditionClassName + " not found");
        }
    }

    public static Class<?> getItemClassByConditionObject(Object conditionObject) {
        if (conditionObject instanceof String) {
            try {
                return Class.forName((String) conditionObject);
            } catch (ClassNotFoundException cnfex) {
                return null;
            }
        } else if (conditionObject instanceof Class) {
            return (Class<?>) conditionObject;
        } else if (conditionObject instanceof BaseModel) {
            return conditionObject.getClass();
        } else {
            return getQueryItemClass(conditionObject);
        }
    }
    
    public static Map<String, Object> toNamedParameters(Object valueBean) {
    	return BeanUtils.toMap(valueBean);
    }

    public static String getSqlUpdateName(Object condition) {
        String updateName = EntityUtils.getNormalName(condition.getClass().getSimpleName());
        if (updateName.endsWith("UpdateCondition")) {
            updateName = updateName.substring(0, updateName.length() - 9);
        }
        return updateName;
    }

    public static Order[] parseOrderByToHibernateOrders(String orderBy) {
        if (orderBy == null || orderBy.trim().length() == 0) {
            return null;
        }
        List<Order> result = new ArrayList<Order>();
        String[] orders = orderBy.split(",");
        for (String order : orders) {
            order = order.trim();
            if (order.length() == 0) {
                continue;
            }
            if (order.toLowerCase().endsWith(" asc")) {
                result.add(Order.asc(EntityUtils.toPascalCase(order.substring(0, order.length() - 4).trim(), false)));
            } else if (order.toLowerCase().endsWith(" desc")) {
                result.add(Order.desc(EntityUtils.toPascalCase(order.substring(0, order.length() - 5).trim(), false)));
            } else {
                result.add(Order.asc(EntityUtils.toPascalCase(order.trim(), false)));
            }
        }
        return result.toArray(new Order[result.size()]);
    }

    public static String addExtraConditions(String sql, String sqlCondition) {
        if (sqlCondition != null && sqlCondition.trim().length() != 0) {
            sqlCondition = "(" + sqlCondition + ")";
            if (sql.indexOf(SQL_EXTRA_CONDITIONS_MACRO) >= 0) {
                sql = sql.replace(SQL_EXTRA_CONDITIONS_MACRO, sqlCondition);
            } else {
                sql = "SELECT T__CONDITION__.* FROM (" + sql + ") T__CONDITION__ WHERE " + sqlCondition;
            }
        } else {
            sql = sql.replace(SQL_EXTRA_CONDITIONS_MACRO, "0=0");
        }
        return sql;
    }
    
    public static boolean queryExists(SessionFactory sessionFactory, String sqlName) {
    	NamedSQLQueryDefinition sqlQuery = ((SessionFactoryImpl) sessionFactory).getNamedSQLQuery(sqlName);
    	return sqlQuery != null;
    }
    

    public static String getNamedSql(SessionFactory sessionFactory, String sqlName) {
        NamedSQLQueryDefinition sqlQuery = ((SessionFactoryImpl) sessionFactory).getNamedSQLQuery(sqlName);
        if (sqlQuery != null) {
            return parseNameSql(sqlQuery.getQueryString());
        } else {
            throw new RuntimeException("Query " + sqlName + " not found");
        }
    }

    public static String parseNameSql(String sql) {
        StringBuilder sb = new StringBuilder();
        StringReader reader = new StringReader(sql);
        int c;
        int c1;
        try {
            while ((c = reader.read()) != -1) {
                if (c == '<') {
                    reader.mark(1);
                    c1 = reader.read();
                    if (c1 != '<') {
                        reader.reset();
                        sb.append((char) c);
                        continue;
                    }
                    // 读取 << >>的内容
                    Set<String> argNames = new HashSet<String>();
                    StringBuilder buff = new StringBuilder();
                    while ((c = reader.read()) != -1) {
                        if (c == ':') {
                            buff.append(':');
                            StringBuilder argName = new StringBuilder();
                            while ((c = reader.read()) != -1) {
                                if (!SQL_SEPARATORSET.contains(c)) {
                                    buff.append((char) c);
                                    argName.append((char) c);
                                    reader.mark(1);
                                    c = reader.read();
                                    reader.reset();
                                    if (SQL_SEPARATORSET.contains(c) || c == -1) {
                                        argNames.add(argName.toString());
                                        break;
                                    }
                                }
                            }
                            continue;
                        }
                        if (c == '>') {
                            reader.mark(1);
                            c1 = reader.read();
                            if (c1 == '>') {
                                break;
                            }
                            reader.reset();
                        }
                        buff.append((char) c);
                    }
                    if (argNames.size() > 0) {// 转换为:<%if(!isEmpty(arg.name))
                                              // {%> <%}%>
                        sb.append("<%if(!(");
                        Iterator<String> iterator = argNames.iterator();
                        for (; iterator.hasNext();) {
                            sb.append("isEmpty(arg.").append(iterator.next()).append(')');
                            if (iterator.hasNext()) {
                                sb.append("&&");
                            }
                        }
                        sb.append(")){%>")
                          //.append(LINE_SEPARATOR)
                          .append(buff)
                          //.append(LINE_SEPARATOR)
                          .append("${''}<%}%>");
                    } else {
                        sb.append(buff);
                    }
                } else {
                    sb.append((char) c);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return sb.toString();
    }

    public static String trimLine(String source){
        return trimLine(new StringBuffer(source));
    }
    
    public static String trimLine(StringBuffer buff){
        int start = 0,current = 0;
        boolean isEmptyLine = true;
        while(current < buff.length()){
            char c = buff.charAt(current);
            if( c == '\n'){
                if(isEmptyLine){
                    buff.delete(start,current + 1);
                    current = start;
                    continue;
                }else{
                    start = current + 1;
                }
                isEmptyLine = true;
            }else if(isEmptyLine && !Character.isWhitespace(c)){
                isEmptyLine = false;
            }
            current++;
        }
        if(buff.charAt(buff.length() - 1) == '\n'){
            buff.deleteCharAt(buff.length() - 1);
        }
        if(buff.charAt(buff.length() - 1) == '\r'){
            buff.deleteCharAt(buff.length() - 1);
        }
        return  buff.toString();
    }
    
    public static String getDynamicSql(SessionFactory sessionFactory, String sqlName, Map<String,Object> valueBean) {
        Template template = QueryTemplate.getQueryTemplate(sessionFactory).getTemplate(sqlName);
        template.binding("arg", valueBean);
        template.binding("user", Context.getCurrentUser());
        StringWriter writer = new StringWriter();
        template.renderTo(writer);
        return trimLine(writer.getBuffer());        
    }
    
    public static String getDynamicSql(SessionFactory sessionFactory, String sqlName, Object valueBean) {
        Map<String,Object> map = BeanUtils.toMap(valueBean);
        return getDynamicSql(sessionFactory,sqlName,map);
    }

    public static TreeMap<Integer, String> getSqlParameters(String sql) {
        ParamLocationRecognizer recognizer = new ParamLocationRecognizer();
        ParameterParser.parse(sql, recognizer);
        TreeMap<Integer, String> map = new TreeMap<Integer, String>();
        for (Map.Entry<String, NamedParameterDescription> entry : recognizer.getNamedParameterDescriptionMap().entrySet()) {
            int[] positions = entry.getValue().buildPositionsArray();
            for (int pos : positions) {
                map.put(pos, entry.getKey());
            }
        }
        return map;
    }

    public static Set<String> getSqlParameterSets(String sql) {
        ParamLocationRecognizer recognizer = new ParamLocationRecognizer();
        ParameterParser.parse(sql, recognizer);
        return recognizer.getNamedParameterDescriptionMap().keySet();
    }
    
	public static StringBuilder addExtraConditions(StringBuilder sql, String sqlCondition) {
		if (!StringUtils.isEmpty(sqlCondition)) {
			int idx = sql.indexOf(SQL_EXTRA_CONDITIONS_MACRO);
			if(idx == -1){
				sql.insert(0, "SELECT T__CONDITION__.* FROM (");
				sql.append(") T__CONDITION__ WHERE ").append('(').append(sqlCondition).append(')');
			}else{
				sql.replace(idx, idx + SQL_EXTRA_CONDITIONS_MACRO.length() , sqlCondition);
			}
		}else{
			int idx = sql.indexOf(SQL_EXTRA_CONDITIONS_MACRO);
			if(idx > 0) {
				sql.delete(idx, idx + SQL_EXTRA_CONDITIONS_MACRO.length());
			}
		}
		return sql;
	}    
	
	public static StringBuilder addOrderBy(StringBuilder sql, String orderBy) {
		if (!StringUtils.isEmpty(orderBy)) {
			orderBy = convertToSqlOrderBy(orderBy,true);
			int idx = sql.indexOf(SQL_EXTRA_ORDER_MACRO);
			if(idx == -1) {
				sql.insert(0, "SELECT * FROM (");
				sql.append(") T_").append(orderBy);
			}else{
				sql.replace(idx, idx + SQL_EXTRA_ORDER_MACRO.length(), orderBy);
			}
		}else{
			int idx = sql.indexOf(SQL_EXTRA_ORDER_MACRO);
			if(idx > 0) {
				sql.delete(idx, idx + SQL_EXTRA_ORDER_MACRO.length());
			}
		}
		return sql;
	}	
	
	public static StringBuilder insert(StringBuilder sql, int offset, String... values) {
		for (String value : values) {
			sql.insert(offset, value);
			offset += value.length();
		}
		return sql;
	}	
	
	/**
	 * 把命名化参数查询转为?占位符查询
	 * 
	 * @param query
	 *            查询语句
	 * @param out
	 *            输出参数名
	 * @return 转换后的查询语句
	 */
	public static String convertToParameterQueryFromNamed(String query, ArrayList<String> out) {
		StringBuilder sbQuery = new StringBuilder();
		StringReader reader = new StringReader(query);
		int c;
		int c1;
		StringBuilder buff = new StringBuilder();
		try {
			while ((c = reader.read()) != -1) {
				if (c == ':') {
					buff.setLength(0);
					while ((c1 = reader.read()) != -1) {
						if (!SQL_SEPARATORSET.contains(c1)) {
							buff.append((char) c1);
							reader.mark(1);
							c1 = reader.read();
							if (SQL_SEPARATORSET.contains(c1) || c1 == -1) {
								out.add(buff.toString());
								sbQuery.append('?');
								if (c1 != -1) {
									sbQuery.append((char) c1);
								}
							}
							reader.reset();
							continue;
						}
						break;
					}
				} else
					sbQuery.append((char) c);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return sbQuery.toString();
	}

	/**
	 * 转换复杂的查询参数,把查询参数(parameter)里的数组或集体转换为查询参数
	 * 
	 * @param query
	 *            sql查询
	 * @param parameter
	 *            查询参数
	 * @return 转换后的sql查询
	 */
	public static StringBuilder convertComplexNamedQueryParameter(StringBuilder query, Map<String, Object> parameter) {
		if (parameter == null || parameter.size() == 0) {
			return query;
		}
		boolean hasComplexParam = false;
		for (Entry<String, Object> entry : parameter.entrySet()) {
			if (entry.getValue() != null) {
				hasComplexParam = entry.getValue() instanceof Collection<?> || entry.getValue().getClass().isArray();
				if (hasComplexParam)
					break;
			}
		}
		if (!hasComplexParam)
			return query;
		StringBuilder temp = new StringBuilder();
		StringBuilder temp1 = new StringBuilder();
		int i, j = 0;
		Object value;
		String key;
		String[] names = parameter.keySet().toArray(new String[0]);
		for (String name : names) {
			value = parameter.get(name);
			if (value instanceof Collection<?>) {
				Collection<?> coll = (Collection<?>) value;
				if (coll.size() > 0) {
					parameter.remove(name);
					i = 0;
					j++;
					temp.setLength(0);
					for (Object val : coll) {
						temp1.setLength(0);
						key = temp1.append(NAMED_PARAMETER_PREFIX_INNER).append(j).append('_').append(i).toString();
						parameter.put(key, val);
						temp.append(':').append(key);
						if (i < coll.size() - 1) {
							temp.append(',');
						}
						i++;
					}
					replaceAllParamName(query, name, temp.append(' ').toString());
					continue;
				}
				parameter.put(name, null);
			} else if (value != null && value.getClass().isArray()) {
				int len = Array.getLength(value);
				if (len > 0) {
					parameter.remove(name);
					j++;
					temp.setLength(0);
					for (i = 0; i < len; i++) {
						temp1.setLength(0);
						key = temp1.append(NAMED_PARAMETER_PREFIX_INNER).append(j).append('_').append(i)
						        .toString();
						parameter.put(key, Array.get(value, i));
						temp.append(':').append(key);
						if (i < len - 1) {
							temp.append(',');
						}
					}
					replaceAllParamName(query, name, temp.append(' ').toString());
					continue;
				}
				parameter.put(name, null);
			}
		}
		return query;
	}

	/**
	 * 转换复杂的查询参数,把查询参数(parameter)里的数组或集体转换为查询参数
	 * 
	 * @param query
	 *            sql查询
	 * @param parameter
	 *            查询参数
	 * @param outParameters
	 *            输出查询参数
	 * @return 转换后的sql查询
	 */
	public static StringBuilder convertComplexQueryParameter(StringBuilder query, Object[] parameter,
	        List<Object> outParameters) {
		if (parameter == null || parameter.length == 0) {
			return query;
		}
		boolean hasComplexParam = false;
		for (Object param : parameter) {
			if (param != null) {
				hasComplexParam = param instanceof Collection<?> || param.getClass().isArray();
				if (hasComplexParam)
					break;
			}
		}
		if (!hasComplexParam)
			return query;
		int idx = 0, count, i = 0;
		for (; idx < query.length(); i++) {
			idx = query.indexOf("?", idx);
			if (idx == -1)
				break;
			count = addToList(parameter[i], outParameters);
			if (count > 0) {
				repeat(query, idx, "?,", count - 1);
				idx += (count - 1) * 2 + 1;
			} else {
				idx++;
			}
		}
		return query;
	}

	public static StringBuilder repeat(StringBuilder sql, int offset, String value, int count) {
		for (int i = 0; i < count; i++) {
			if (offset < 0)
				sql.append(value);
			else
				sql.insert(offset, value);
		}
		return sql;
	}
	
	private static int addToList(Object val, List<Object> out) {
		if (val instanceof Collection<?>) {
			Collection<?> coll = (Collection<?>) val;
			if (coll.size() == 0) {
				out.add(null);
				return 0;
			}
			out.addAll(coll);
			return coll.size();
		} else if (val != null && val.getClass().isArray()) {
			int len = Array.getLength(val);
			if (len == 0) {
				out.add(null);
				return 0;
			}
			for (int i = 0; i < len; i++) {
				out.add(Array.get(val, i));
			}
			return len;
		}
		out.add(val);
		return 0;
	}
	
	/**
	 * 把查询中的?参数占位符转换为命名化参数查询
	 * 
	 * @param query
	 *            查询sql
	 * @param parameter
	 *            查询参数
	 * @param outParameter
	 *            输出转换后查询参数
	 * @return 转换后的查询sql
	 */
	public static String convertToNamedQueryFromParameter(String query, Object[] parameter,
	        Map<String, Object> outParameter) {
		if (parameter == null)
			parameter = new Object[0];
		StringBuilder sb = new StringBuilder(query);
		for (int count = 0, idx = 0; idx < sb.length(); idx++) {
			if (sb.charAt(idx) == '?') {
				sb.deleteCharAt(idx).insert(idx, ':' + NAMED_PARAMETER_PREFIX + count);
				outParameter.put(NAMED_PARAMETER_PREFIX + count, count < parameter.length ? parameter[count] : null);
				count++;
			}
		}
		return sb.toString();
	}

	public static Object[] mergeParameters(Object[] params1, Object[] params2) {
		if (params2 == null || params2.length == 0) {
			return params1;
		}
		if (params1 == null || params1.length == 0) {
			return params2;
		}
		Object[] array = new Object[params1.length + params2.length];
		System.arraycopy(params1, 0, array, 0, params1.length);
		System.arraycopy(params2, 0, array, params1.length, params2.length);
		return array;
	}

	/**
	 * 把 dictCode asc,dictCalue desc转换为dict_code asc,dict_value desc
	 * 
	 * @param orderBy
	 * @return
	 */
	public static String convertToSqlOrderBy(String orderBy,boolean addOrderBy) {
		if (StringUtils.isEmpty(orderBy)){
			return "";
		}
		String[] array = StringUtils.tokenizeToStringArray(orderBy, " ,`\"[]");
		if (array != null) {
			StringBuilder sb = new StringBuilder();
			if(addOrderBy) {
				sb.append(" order by ");
			}
			int nTemp;
			String sTemp;
			for (int i = 0; i < array.length; i++) {
				sTemp = array[i];
				if (sTemp.equalsIgnoreCase("delete") || sTemp.equalsIgnoreCase("select")
				        || sTemp.equalsIgnoreCase("insert") || sTemp.equalsIgnoreCase("asc")
				        || sTemp.equalsIgnoreCase("desc")) {
					continue;
				}
				for (char ch : sTemp.toCharArray()) {
					if (Character.isUpperCase(ch)) {
						sb.append('_').append(Character.toLowerCase(ch));
					} else
						sb.append(ch);
				}
				nTemp = i + 1;
				if (nTemp < array.length) {
					sTemp = array[nTemp];
					if (sTemp.equalsIgnoreCase("asc") || sTemp.equalsIgnoreCase("desc")) {
						sb.append(' ').append(sTemp);
						i = nTemp;
					}
				}
				if ((i + 1) < array.length)
					sb.append(',');
			}
			return sb.toString();
		}
		return "";
	}	

	public static int replaceAllParamName(StringBuilder sql, String name, String value) {
		if (StringUtils.isEmpty(name) || StringUtils.isEmpty(value)) {
			return -1;
		}
		name = ":".concat(name);
		int idx = 0;
		for (; idx < sql.length();) {
			idx = replaceParamName(sql, name, value, idx);
			if (idx == -1) {
				break;
			}
		}
		return 0;
	}

	private static int replaceParamName(StringBuilder sql, String name, String value, int startIdx) {
		int idx = sql.indexOf(name, startIdx);
		if (idx == -1)
			return -1;
		int _idx = idx + name.length();
		if (_idx < sql.length()) {
			char ch = sql.charAt(_idx);
			if (!SQL_SEPARATORSET.contains((int) ch)) {
				return _idx;
			}
		}
		sql.replace(idx, idx + name.length(), value);
		return idx + value.length();
	}	
}
