package com.harvey.w.core.config;

import java.util.Properties;

public interface ConfigWrapper {
    String get(String key);

    String get(String key, String defVal);

    RunEnv getRunEnv();

    Properties getProperties();
    
    ConfigWrapper Instance = new ConfigWrapper() {
        
        @Override
        public RunEnv getRunEnv() {
            return Config.getRunEnv();
        }
        
        @Override
        public Properties getProperties() {
            return Config.getProperties();
        }
        
        @Override
        public String get(String key, String defVal) {
            return Config.get(key, defVal);
        }
        
        @Override
        public String get(String key) {
            return Config.get(key);
        }
    };
}
