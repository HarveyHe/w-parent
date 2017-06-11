package com.harvey.w.dubbo.constant;

import com.alibaba.dubbo.common.Constants;
import com.alibaba.dubbo.common.utils.NetUtils;
import com.alibaba.dubbo.config.ProtocolConfig;
import com.alibaba.dubbo.config.RegistryConfig;

public final class Constant {

	public static final RegistryConfig NARegistry = new RegistryConfig(RegistryConfig.NO_AVAILABLE);

	public static final ProtocolConfig InJvmProtocolConfig = new ProtocolConfig(Constants.LOCAL_PROTOCOL, 0) {
		private static final long serialVersionUID = 1L;

		{
			setHost(NetUtils.LOCALHOST);
		}
	};
	public static final ProtocolConfig DubboProtocalConfig = new ProtocolConfig(Constants.DEFAULT_PROTOCOL);
	
	public static final String DubboConsumeService = "w.dubbo.consumeService";
	public static final String DubboExposeService = "w.dubbo.exposeService";
	public static final String DubboBaseService ="w.dubbo.baseService";
}
