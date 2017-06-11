package com.harvey.w.dubbo.provider;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.harvey.w.dubbo.constant.Constant;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.StringUtils;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.config.AbstractServiceConfig;
import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.ProviderConfig;
import com.alibaba.dubbo.config.RegistryConfig;
import com.alibaba.dubbo.config.ServiceConfig;
import com.harvey.w.core.config.Config;

public class ProviderWrapper {
	private List<DubboProviderConfig> providers = Collections.emptyList();
	private ProviderConfig defaultProvider;
	private Set<String> serviceConfigProps = Collections.emptySet();
	private ApplicationConfig applicationConfig;
	private List<RegistryConfig> registries;
	private List<ProtocolConfig> protocols;
	private static ProviderWrapper pw;
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

	public static ProviderWrapper wrap(ApplicationConfig applicationConfig, List<ProviderConfig> providerConfigs, List<RegistryConfig> registries,
			List<ProtocolConfig> protocols) {
		if (pw != null) {
			return pw;
		}
		pw = new ProviderWrapper();
		pw.applicationConfig = getApplicationConfig(applicationConfig);
		pw.registries = registries;
		pw.protocols = protocols;
		if (providerConfigs != null) {
			pw.providers = new ArrayList<DubboProviderConfig>();
			for (ProviderConfig pc : providerConfigs) {
				if (pc instanceof DubboProviderConfig) {
					pw.providers.add((DubboProviderConfig) pc);
				} else if (pw.defaultProvider == null && Boolean.TRUE.equals(pc.isDefault())) {
					pw.defaultProvider = pc;
				}
			}
			pw.serviceConfigProps = new HashSet<String>();
			BeanWrapper bw = new BeanWrapperImpl(new EmptyServiceConfig());
			for (PropertyDescriptor pd : bw.getPropertyDescriptors()) {
				if (pd.getReadMethod() != null && pd.getWriteMethod() != null) {
					pw.serviceConfigProps.add(pd.getName());
				}
			}
		}
		return pw;
	}

	private ProviderWrapper() {
	}

	public ProviderWrapper apply(ServiceConfig<?> sc) {
		ProviderConfig pc = null;
		for (DubboProviderConfig dpc : this.providers) {
			if (dpc.isSupport(sc.getRef())) {
				pc = dpc;
				break;
			}
		}
		if (pc == null) {
			pc = this.defaultProvider;
		}
		if (pc != null) {
			sc.setProvider(pc);
			// copy property value to service config
			BeanWrapper bwSc = new BeanWrapperImpl(sc);
			BeanWrapper bwPc = new BeanWrapperImpl(sc);
			for (String prop : this.serviceConfigProps) {
				Object value = bwPc.getPropertyValue(prop);
				if (value != null) {
					bwSc.setPropertyValue(prop, value);
				}
			}
		}
		if (sc.getApplication() == null && (sc.getProvider() == null || sc.getProvider().getApplication() == null)) {
			sc.setApplication(applicationConfig);
		}
		if (sc.getRegistry() == null && 
				(sc.getProvider() == null || sc.getProvider().getRegistry() == null) && 
				 (applicationConfig == null || applicationConfig.getRegistry() == null)) {
			if (this.registries != null && this.registries.size() > 0) {
				sc.setRegistries(registries);
			} else {
				sc.setRegistry(Constant.NARegistry);
				sc.setScope(Constants.SCOPE_LOCAL);
			}
		}		
		if(sc.getProtocol() == null && 
				(sc.getProvider() == null || sc.getProvider().getProtocol() == null)){
			if(this.protocols != null && protocols.size() > 0){
				sc.setProtocols(protocols);
			}else{
				sc.setProtocol(Constant.DubboProtocalConfig);
			}
		}
		return this;
	}

	public static void unwrap() {
		if (pw != null) {
			pw.defaultProvider = null;
			pw.providers = null;
			pw.serviceConfigProps = null;
			pw.applicationConfig = null;
			pw.registries = null;
			pw.protocols = null;
			pw = null;
			defaultApplicationConfig = null;
		}
	}
	
	static class EmptyServiceConfig extends AbstractServiceConfig{

        /**
         * 
         */
        private static final long serialVersionUID = 1L;
	    
        public EmptyServiceConfig(){
            
        }
	}
}
