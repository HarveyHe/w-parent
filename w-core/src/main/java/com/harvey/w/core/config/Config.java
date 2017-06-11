package com.harvey.w.core.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;

import com.harvey.w.core.utils.ResourceUtils;

public final class Config {
    private static final Properties config = new Properties();
    private static final String CONFIG_LOCATION = "config.properties";
    private static final String RUN_ENV_KEY = "sys.runEnv";

    private static RunEnv runEnv = null;

    static {
        try {
            String filepath = null;
            //default use current folder config.properties
            File file = new File(CONFIG_LOCATION);
            if (file.exists() && file.isFile() && file.canRead()) {
                filepath = file.getCanonicalPath();
                FileInputStream is = new FileInputStream(file);
                ConfigLoader.loadProperties(config, is);
            } else {
                Resource resource = ResourceUtils.getResource("classpath:/" + CONFIG_LOCATION);
                filepath = resource.getURI().toString();
                InputStream is = resource.getInputStream();
                ConfigLoader.loadProperties(config, is);
            }
            System.out.println("-------------Eaf Core Confing Information--------------");
            System.out.println(filepath);
            System.out.println("-------------------------------------------------------");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    static void put(String key, String value) {
        config.put(key, value);
    }

    public static String get(String key) {
        return config.getProperty(key);
    }

    public static String get(String key, String defVal) {
        return config.getProperty(key, defVal);
    }

    public static RunEnv getRunEnv() {
        if (runEnv == null) {
            String key = get("sys.runEnvKey");
            if (StringUtils.isNotBlank(key)) {
                key = System.getenv(key);
                if (StringUtils.isNotBlank(key)) {
                    try {
                        runEnv = Enum.valueOf(RunEnv.class, key);
                    } catch (Exception ex) {

                    }
                }
            }
            if (runEnv == null) {
                try {
                    String env = System.getProperty("runEnv", get(RUN_ENV_KEY, "dev"));
                    runEnv = Enum.valueOf(RunEnv.class, env);
                } catch (Exception ex) {
                    runEnv = RunEnv.dev;
                }
            }
        }
        return runEnv;
    }

    public static Properties getProperties() {
        Properties properties = new Properties();
        properties.putAll(config);
        return properties;
    }

    public static void mergeProperties(Properties properties) {
        config.putAll(properties);
    }
}
