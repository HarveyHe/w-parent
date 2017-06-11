package com.harvey.w.core.utils;

import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.harvey.w.core.bean.DefaultBeanWrapper;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;
import org.apache.commons.lang3.reflect.MethodUtils;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.cglib.beans.BeanMap;
import org.springframework.context.ApplicationContext;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

/**
 * bean工具
 * 
 * @author dream.chen
 * 
 */
public class BeanUtils {

	private static final DefaultBeanWrapper BeanWrapper = new DefaultBeanWrapper();

	public static void copy(Collection<?> from, Collection<?> to) {
		Iterator<?> itFrom = from.iterator();
		Iterator<?> itTo = to.iterator();
		for (; itFrom.hasNext() && itTo.hasNext();) {
			copy(itFrom.next(), itTo.next());
		}
	}

	public static void copy(Object from, Object to) {
		copy(from, to, null);
	}

	public static void copy(Object from, Object to, List<String> validFields) {
		Map<String, Object> mapFrom = toMap(from);
		Map<String, Object> mapTo = toMap(to);
		Set<String> keysFrom = mapFrom.keySet();
		BeanMap beanMapTo = mapTo instanceof BeanMap ? (BeanMap) mapTo : null;
		if (validFields != null && !validFields.isEmpty()) {
			Set<String> keysTo = mapTo.keySet();
			for (String key : validFields) {
				if (!keysFrom.contains(key) || !keysTo.contains(key)) {
					continue;
				}
				Object val = mapFrom.get(key);
				if (val != null && beanMapTo != null) {
					Class<?> requiredType = beanMapTo.getPropertyType(key);
					if (!val.getClass().equals(requiredType)) {
						val = BeanWrapper.convertIfNecessary(val, requiredType);
					}
				}
				mapTo.put(key, val);
			}
			return;
		}
		for (String key : mapTo.keySet()) {
			if (!keysFrom.contains(key)) {
				continue;
			}
			Object val = mapFrom.get(key);
			if (val != null && beanMapTo != null) {
				Class<?> requiredType = beanMapTo.getPropertyType(key);
				if (!val.getClass().equals(requiredType)) {
					val = BeanWrapper.convertIfNecessary(val, requiredType);
				}
			}
			mapTo.put(key, val);
		}
	}

	@SuppressWarnings("unchecked")
    public static Map<String, Object> toMap(Object valueBean, Class<?> beanClass) {
		if(valueBean == null || beanClass == null) {
			return null;
		}
		if (valueBean instanceof Map<?, ?>) {
			return (Map<String, Object>) valueBean;
		}
		BeanMap.Generator generator = new BeanMap.Generator();
		generator.setUseCache(true);
		generator.setBean(valueBean);
		generator.setBeanClass(beanClass);
		return generator.create();
	}

	public static Map<String, Object> toMap(Object valueBean) {
		if(valueBean == null) {
			return null;
		}
		Class<?> clazz = ClassUtils.getUserClass(valueBean);
		return toMap(valueBean, clazz);
	}

	/**
	 * like oracle decode function
	 * 
	 * @param value
	 *            返回值
	 * @param args
	 *            判断参数
	 * @return 返回条件成立的参数
	 */
	public static Object decode(Object value, Object... args) {
		int i = 0;
		while (true) {
			if ((value == null && args[i] == null) || value.equals(args[i])) {
				return args[i + 1];
			} else {
				if (args.length == i + 3) {
					// default
					return args[i + 2];
				} else {
					i = i + 2;
					continue;
				}
			}
		}
	}

	/**
	 * 类型转换
	 * 
	 * @param value
	 *            转换值
	 * @param clazz
	 *            转换类型
	 * @return 转换后的值
	 */
	public static <T> T convert(Object value, Class<T> clazz) {
		if (StringUtils.isEmpty(value)){
			return null;
		}else if(clazz.isEnum() || Date.class.isAssignableFrom(clazz) || value instanceof Date || clazz.isArray() || value.getClass().isArray()){
			return BeanWrapper.convertIfNecessary(value, clazz);
		}
		Converter converter = ConvertUtils.lookup(value.getClass(), clazz);
		if(converter != null) {
			return converter.convert(clazz, value);
		}
		return null;
	}

	public static <T> T convert(Object value,T def, Class<T> clazz) {
		T val = null;
		try{
			val = convert(value,clazz);
		}catch(Exception ex){
		}
		return val == null ? def : val;
	}
	/**
	 * like mysql ifnull function
	 * 
	 * @param args
	 *            判断参数数组
	 * @return 返回非空的参数
	 */
	@SafeVarargs
	public static <T> T ifnull(T... args) {
		for (T arg : args) {
			if (!StringUtils.isEmpty(arg)) {
				return arg;
			}
		}
		return null;
	}

	public static <T> T of(Object inst, Class<T> clazz) {
		if (inst == null || clazz == null || !clazz.isInstance(inst)) {
			return null;
		}
		return clazz.cast(inst);
	}
	
	public static Map<String,Class<?>> getBeanPropertyTypeMap(Class<?> beanClass) throws Exception {
        BeanInfo info = Introspector.getBeanInfo(beanClass, Object.class);
        PropertyDescriptor[] all = info.getPropertyDescriptors();	
        Map<String,Class<?>> map = new HashMap<>();
        for(PropertyDescriptor pd : all) {
            if(pd.getWriteMethod() != null && pd.getReadMethod() != null &&  pd.getPropertyType() != null) {
               map.put(pd.getName(), pd.getPropertyType());
            }
        }
        return map;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getBean(ApplicationContext ctx, String beanId, T def) {
		if (StringUtils.isEmpty(beanId))
			return def;
		try {
			return (T) ctx.getBean(beanId);
		} catch (NoSuchBeanDefinitionException ex) {
			return def;
		}
	}
	
	public static <T> T getBean(ApplicationContext ctx, Class<T> type, T def) {
		try {
			return ctx.getBean(type);
		} catch (NoSuchBeanDefinitionException ex) {
			return def;
		}
	}	
	
	public static <T> T getBean(ApplicationContext ctx, String beanId, Class<T> type, T def) {
		try {
			return ctx.getBean(beanId,type);
		} catch (Exception ex) {
			return def;
		}
	}	

	public static <T> T detectBean(ApplicationContext ctx, String beanTypeOrName, Class<T> requiredType,boolean instantiateType) throws Exception {
		Assert.notNull(beanTypeOrName, "beanTypeOrName can't be null");
		Assert.notNull(requiredType, "required type can't be null");
		if (beanTypeOrName.startsWith("bean:")) {
			beanTypeOrName = beanTypeOrName.substring(5);
			return ctx.getBean(beanTypeOrName, requiredType);
		}
		Class<?> beanType = Class.forName(beanTypeOrName);
		Assert.isAssignable(requiredType, beanType,"bean type:" + beanTypeOrName + " can't be assignable from:" + requiredType.getName());
		Collection<?> beans = ctx.getBeansOfType(beanType).values();
		if(!beans.isEmpty()) return requiredType.cast(beans.iterator().next());
		if(!instantiateType) return null;
		try{
		    return org.springframework.beans.BeanUtils.instantiateClass(beanType, requiredType);
		}catch(Exception ex) {
		    throw new Exception("can't be detect bean:"+beanTypeOrName,ex);
		}
	}

	public static boolean isSimpleValueType(Class<?> clazz) {
		return org.springframework.beans.BeanUtils.isSimpleValueType(clazz);
	}
	
	public static Object invokeBeanMethod(ApplicationContext ctx,String beanName,String methodName,Object[] params) throws Exception {
		Object bean = ctx.getBean(beanName);
		return MethodUtils.invokeMethod(bean,methodName,params);
	}
	
	@SuppressWarnings("unchecked")
    public static <T1,T2> Map<T1,T2> getStartsWith(Map<T1,T2> config,String prefix) {
		if (StringUtils.isEmpty(prefix) || config == null){
			return Collections.emptyMap();
		}
		Map<T1,T2> result = new HashMap<>();
		String key;
		for (Entry<T1, T2> entry : config.entrySet()) {
			key = entry.getKey().toString();
			if (key.startsWith(prefix)) {
				result.put((T1)key,entry.getValue());
			}
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
    public static <T1,T2> Map<T1,T2> getStartsWithAndTrim(Map<T1,T2> config,String prefix) {
		if (StringUtils.isEmpty(prefix) || config == null){
			return Collections.emptyMap();
		}
		Map<T1,T2> result = new HashMap<>();
		String key;
		for (Entry<T1, T2> entry : config.entrySet()) {
			key = entry.getKey().toString();
			if (key.startsWith(prefix)) {
				key = key.substring(prefix.length());
				result.put((T1)key,entry.getValue());
			}
		}
		return result;
	}
}
