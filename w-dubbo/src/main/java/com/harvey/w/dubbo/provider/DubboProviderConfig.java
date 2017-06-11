package com.harvey.w.dubbo.provider;

import java.util.List;

import com.alibaba.dubbo.config.ProviderConfig;

/**
 * 自动暴露service使用的默认provider
 * 
 * @see com.alibaba.dubbo.config.ProtocolConfig
 * @see com.alibaba.dubbo.config.ServiceConfig
 * @author william.liangf
 * @export
 */
public class DubboProviderConfig extends ProviderConfig {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private List<Class<?>> supportClass;
	private List<Class<?>> unsupportClass;
	
	public boolean isSupport(Object service){
		if(service == null || (supportClass == null || supportClass.isEmpty()) && (unsupportClass == null || unsupportClass.isEmpty())){
			return Boolean.TRUE.equals(super.isDefault());
		}
		Class<?> serviceClass = service.getClass();
		if(supportClass != null){
			for(Class<?> clazz : supportClass){
				if(clazz.isAssignableFrom(serviceClass)){
					return true;
				}
			}
		}
		if(unsupportClass != null){
			for(Class<?> clazz : unsupportClass){
				if(clazz.isAssignableFrom(serviceClass)){
					return false;
				}
			}
		}
		return Boolean.TRUE.equals(super.isDefault());
	}

	public List<Class<?>> getSupportClass() {
		return supportClass;
	}

	public void setSupportClass(List<Class<?>> supportClass) {
		this.supportClass = supportClass;
	}

	public List<Class<?>> getUnsupportClass() {
		return unsupportClass;
	}

	public void setUnsupportClass(List<Class<?>> unsupportClass) {
		this.unsupportClass = unsupportClass;
	}

}
