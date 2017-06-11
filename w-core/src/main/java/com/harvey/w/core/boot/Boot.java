package com.harvey.w.core.boot;

import com.harvey.w.core.config.Config;
import com.harvey.w.core.spring.ContextConfigInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * bootstrap w
 * 
 */
public class Boot {
	private static final Object MUTEX = new Object();
	private static final String IS_MUTEX = "1";

	public static void main(String[] args) throws Exception {
		String configLocations = args != null && args.length > 0 ? args[0] : Config.get("sys.configLocations");
		final ConfigurableApplicationContext context = ContextConfigInitializer.createApplicationContext(configLocations);
		System.out.println("Eaf is successfully to startup!");
		String isMutex = args != null && args.length > 1 ? args[1] : IS_MUTEX;
		if (IS_MUTEX.equals(isMutex)) {
			Runtime.getRuntime().addShutdownHook(new Thread() {
				@Override
				public void run() {
					context.stop();
					context.close();
					MUTEX.notify();
				}

			});			
			synchronized (MUTEX) {
				while (context.isActive()) {
					MUTEX.wait();
				}
			}
		}
	}

}
