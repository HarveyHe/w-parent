package com.harvey.w.dubbo.consumer;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.AbstractReferenceConfig;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ConsumerConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.harvey.w.core.config.Config;

public class ConsumerWrapper {
	private List<DubboConsumerConfig> consumers = Collections.emptyList();
	private ConsumerConfig defaultConsumer;
	private Set<String> consumerConfigProps = Collections.emptySet();
	private ApplicationConfig applicationConfig;
	private List<RegistryConfig> registries;

	private static ConsumerWrapper wrapper;
	private static ApplicationConfig defaultApplicationConfig;
	
	private static ApplicationConfig getApplicationConfig(ApplicationConfig applicationConfig) {
	    if(applicationConfig != null) {
	        return applicationConfig;
	    }
	    if(defaultApplicationConfig== null) {
	        String appName = Config.get("w.dubbo.application.name");
	        if(!StringUtils.isEmpty(appName)){
	            defaultApplicationConfig = new ApplicationConfig(appName);
	        }
	    }
	    return defaultApplicationConfig;
	}
	
	public static void unwrap() {
		if (wrapper != null) {
			wrapper.consumerConfigProps = null;
			wrapper.defaultConsumer = null;
			wrapper.consumerConfigProps = null;
			wrapper.registries = null;
			wrapper.applicationConfig = null;
			wrapper = null;
			defaultApplicationConfig = null;
		}
	}
	
	public static ConsumerWrapper wrap(ApplicationConfig applicationConfig, List<ConsumerConfig> consumerConfigs, List<RegistryConfig> registries) {
		if (wrapper != null) {
			return wrapper;
		}
		ConsumerWrapper cw = new ConsumerWrapper();
		cw.applicationConfig = getApplicationConfig(applicationConfig);
		cw.registries = registries;
		if (consumerConfigs != null) {
			cw.consumers = new ArrayList<DubboConsumerConfig>();
			for (ConsumerConfig dc : consumerConfigs) {
				if (dc instanceof DubboConsumerConfig) {
					cw.consumers.add((DubboConsumerConfig) dc);
				} else if (cw.defaultConsumer == null && (Boolean.TRUE.equals(dc.isDefault()) || consumerConfigs.size() == 1)) {
					cw.defaultConsumer = dc;
				}
			}
			cw.consumerConfigProps = new HashSet<String>();
			BeanWrapper bw = new BeanWrapperImpl(new EmptyReferenceConfig());
			for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
				if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
					cw.consumerConfigProps.add(pd.getName());
				}
			}
		}
		return wrapper = cw;
	}

	private ConsumerWrapper() {
	}

	public ConsumerWrapper apply(ReferenceConfig<?> referenceConfig) {
		ConsumerConfig cc = null;
		for (DubboConsumerConfig dcc : this.consumers) {
			if (dcc.isSupport(referenceConfig.getInterfaceClass())) {
				cc = dcc;
				break;
			}
		}
		if (cc == null) {
			cc = this.defaultConsumer;
		}
		if (cc != null) {
			referenceConfig.setConsumer(cc);
			// copy property value to consumer config
			BeanWrapper bwRc = new BeanWrapperImpl(referenceConfig);
			BeanWrapper bwCc = new BeanWrapperImpl(cc);
			for (String prop : this.consumerConfigProps) {
				Object value = bwCc.getPropertyValue(prop);
				if (value != null) {
					bwRc.setPropertyValue(prop, value);
				}
			}
		}
		if (referenceConfig.getApplication() == null
				&& (referenceConfig.getConsumer() == null || referenceConfig.getConsumer().getApplication() == null)) {
			referenceConfig.setApplication(applicationConfig);
		}
		if (referenceConfig.getRegistry() == null && 
				(referenceConfig.getConsumer() == null || referenceConfig.getConsumer().getRegistry() == null) &&
				(applicationConfig == null || applicationConfig.getRegistry() == null)) {
			if (registries != null && registries.size() > 0) {
				referenceConfig.setRegistries(registries);
			} else {
				referenceConfig.setScope(Constants.SCOPE_LOCAL);
			}
		}		
		return this;
	}

	static class EmptyReferenceConfig extends AbstractReferenceConfig{

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
        
        public EmptyReferenceConfig(){
            
        }
	    
	}
}
