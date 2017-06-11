package com.harvey.w.core.config;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.TreeMap;

import javax.servlet.ServletContext;

import com.harvey.w.core.config.listener.ContextConfigListener;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.core.OrderComparator;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import com.harvey.w.core.config.listener.ServletContextConfigListener;
import com.harvey.w.core.utils.DetectEncoding;
import com.harvey.w.core.utils.ResourceUtils;

public class ConfigLoader {
    private static final String ConfigLocationSuffix = "-configlocation";
    private static final String PropertiesSuffix = ".properties";
    private static final String EafBundleConfig = "classpath*:/META-INF/w-bundle.properties";

    private static ConfigLoader configLoader;

    private Map<String, Resource> appPropMap = new TreeMap<String, Resource>();
    private Map<String, Resource> bundlePropMap = new TreeMap<String, Resource>();
    private final List<String> appContextList = new ArrayList<String>();
    private final List<String> servletContextList = new ArrayList<String>();
    private final List<String> propRunModesSuffix = new ArrayList<String>();
    private final List<String> xmlRunModesSuffix = new ArrayList<String>();
    private final List<ContextConfigListener> contextConfigListeners = new ArrayList<ContextConfigListener>();
    private final List<ServletContextConfigListener> servletContextConfigListeners = new ArrayList<ServletContextConfigListener>();
    private final String propRunModeSuffix;
    private final String xmlRunModeSuffix;

    public static ConfigLoader loadConfig(ServletContext context) {
        if (configLoader == null) {
            configLoader = new ConfigLoader(context);
        }
        return configLoader;
    }

    public static ConfigLoader loadConfig(Map<String, String> configMap) {
        if (configLoader == null) {
            configLoader = new ConfigLoader(configMap);
        }
        return configLoader;
    }

    public static ConfigLoader loadConfig(String configLocation) {
        if (configLoader == null) {
            Map<String, String> configMap = new HashMap<String, String>();
            configMap.put("default-ConfigLocation", StringUtils.isEmpty(configLocation) ? "classpath*:/spring/*.xml,classpath*:/*.properties" : configLocation);
            configLoader = new ConfigLoader(configMap);
        }
        return configLoader;
    }

    public static ConfigLoader getConfigLoader() {
        return loadConfig((String) null);
    }

    private ConfigLoader(ServletContext context) {
        this(getServletInitParameters(context));
    }

    private ConfigLoader(Map<String, String> configMap) {
        RunEnv mode = Config.getRunEnv();
        propRunModeSuffix = "-" + mode.toString() + PropertiesSuffix;
        xmlRunModeSuffix = "-" + mode.toString() + ".xml";
        for (RunEnv runMode : RunEnv.values()) {
            if (!runMode.equals(mode)) {
                propRunModesSuffix.add("-" + runMode.toString() + PropertiesSuffix);
                xmlRunModesSuffix.add("-" + runMode.toString() + ".xml");
            }
        }
        loadConfigInternal(configMap);
    }

    public List<String> getAppContextList() {
        return appContextList;
    }

    public List<String> getServletContextList() {
        return servletContextList;
    }

    public List<ContextConfigListener> getContextConfigListeners() {
        return contextConfigListeners;
    }

    public List<ServletContextConfigListener> getServletContextConfigListeners() {
        return servletContextConfigListeners;
    }

    public List<String> getServletContextList(String pattern) {
        List<String> list = new ArrayList<String>();
        for (String path : servletContextList) {
            if (ResourceUtils.isMatch(path, pattern)) {
                list.add(path);
            }
        }
        return list;
    }

    private void loadBundleConfig() {
        try {
            List<Resource> bundles = ResourceUtils.getResources(EafBundleConfig);
            for (Resource resource : bundles) {
                Properties bundleProp = loadProperties(resource);
                String name = bundleProp.getProperty("bundle.name");
                String version = bundleProp.getProperty("bundle.version");
                String description = bundleProp.getProperty("bundle.description");
                String configLocation = bundleProp.getProperty("bundle.configLocation");
                String configListener = bundleProp.getProperty("bundle.configListener");
                printBundleInfo(name, version, description, configLocation, configListener);
                String[] locations = ResourceUtils.tokenizeToStringArray(configLocation);
                for (String location : locations) {
                    if (ResourceUtils.isPattern(location)) {
                        processLocationPattern(location);
                    } else {
                        processLocation(location, null, bundlePropMap);
                    }
                }
                if (StringUtils.isEmpty(configListener)) {
                    continue;
                }
                String[] listeners = ResourceUtils.tokenizeToStringArray(configListener);
                for (String listener : listeners) {
                    try {
                        Class<?> clazz = Class.forName(listener);
                        Object instance = BeanUtils.instantiate(clazz);
                        if (instance instanceof ContextConfigListener) {
                            this.contextConfigListeners.add((ContextConfigListener) instance);
                        } else if (instance instanceof ServletContextConfigListener) {
                            this.servletContextConfigListeners.add((ServletContextConfigListener) instance);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
            OrderComparator.sort(this.contextConfigListeners);
            OrderComparator.sort(this.servletContextConfigListeners);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    private static void printBundleInfo(String name, String version, String description, String configLocation, String configListener) {
        System.out.println("-------------Eaf Bundle Information--------------");
        System.out.append("Bundle name:").append(name).println();
        System.out.append("Bundle version:").append(version).println();
        System.out.append("Bundle description:").append(description).println();
        System.out.append("Bundle configLocation:").append(configLocation).println();
        System.out.append("Bundle configListener:").append(configListener).println();
        System.out.println("-------------------------------------------------");
    }

    private void printLoadedConfigInfo() {
        try {
            System.out.println("#############Eaf Loaded Properties######################");
            for (Entry<String, Resource> entry : appPropMap.entrySet()) {
                System.out.println(entry.getValue().getURI());
            }
            System.out.println("#############Eaf Loaded ApplicationContext Xml##########");
            for (String ctx : appContextList) {
                System.out.println(ctx);
            }
            System.out.println("#############Eaf Loaded ServletContext Xml#############");
            for (String ctx : this.servletContextList) {
                System.out.println(ctx);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void loadConfigInternal(Map<String, String> configMap) {
        loadBundleConfig();
        if (configMap != null) {
            for (Entry<String, String> entry : configMap.entrySet()) {
                if (!StringUtils.endsWithIgnoreCase(entry.getKey(), ConfigLocationSuffix)) {
                    continue;
                }
                String[] locations = ResourceUtils.tokenizeToStringArray(entry.getValue());
                for (String location : locations) {
                    try {
                        if (ResourceUtils.isPattern(location)) {
                            processLocationPattern(location);
                        } else {
                            processLocation(location, null, appPropMap);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        }
        processLoadedXml(appContextList);
        processLoadedXml(servletContextList);
        try {
            processLoadedProperties();
        } catch (Exception e) {
            e.printStackTrace();
        }
        printLoadedConfigInfo();
    }

    private void processLoadedProperties() throws Exception {
        Properties logConfig = new Properties();
        for (Entry<String, Resource> entry : appPropMap.entrySet()) {
            InputStream is = entry.getValue().getInputStream();
            if (((String) entry.getKey()).contains("log4j")) {
                // logConfig.load(is);
                loadProperties(logConfig, is);
            } else {
                Properties properties = new Properties();
                loadProperties(properties, is);
                Config.mergeProperties(properties);
            }

        }
        configLog4j(logConfig);
        for (Entry<String, Resource> entry : bundlePropMap.entrySet()) {
            Properties properties = loadProperties(entry.getValue());
            for (Entry<Object, Object> propEntry : properties.entrySet()) {
                if (propEntry.getValue() != null && StringUtils.isEmpty(Config.get(propEntry.getKey().toString()))) {
                    Config.put(propEntry.getKey().toString(), propEntry.getValue() + "");
                }
            }
        }
    }

    private static void configLog4j(Properties logConfig) {
        try {
            Class<?> clazz = Class.forName("org.apache.log4j.PropertyConfigurator");
            clazz.getDeclaredMethod("configure", Properties.class).invoke(null, logConfig);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void processLoadedXml(List<String> target) {
        List<String> tempList = new ArrayList<String>(target);
        for (String path : tempList) {
            if (StringUtils.endsWithIgnoreCase(path, xmlRunModeSuffix)) {
                String path1 = path.replace(xmlRunModeSuffix, ".xml");
                target.remove(path1);
            }
        }
    }

    private void processLocationPattern(String location) throws Exception {
        List<Resource> resources = ResourceUtils.getResources(location);
        for (Resource resource : resources) {
            location = ResourceUtils.getResourcePath(resource);
            processLocation(location, resource, appPropMap);
        }
    }

    private void processLocation(String location, Resource origResource, Map<String, Resource> propMap) throws Exception {

        if (isXmlLocation(location)) {
            List<String> target = null;
            if (isAppContextLocation(location)) {
                target = appContextList;
            } else if (isServletContextLocation(location, "*")) {
                target = servletContextList;
            }
            if (target != null) {
                processXmlConfigs(target, location);
            }
        } else if (isPropertiesLocation(location)) {
            if (ResourceUtils.isPattern(location)) {
                List<Resource> resources = ResourceUtils.getResources(location);
                for (Resource resource : resources) {
                    processPropertiesConfigs(resource, propMap);
                }
            } else if (origResource != null) {
                processPropertiesConfigs(origResource, propMap);
            } else {
                Resource resource = ResourceUtils.getResource(location);
                processPropertiesConfigs(resource, propMap);
            }
        }
    }

    private static Boolean isAppContextLocation(String location) {
        location = location.toLowerCase();
        return ResourceUtils.isMatch(location, "*applicationcontext*.xml") || ResourceUtils.isMatch(location, "**/*applicationcontext*.xml") || ResourceUtils.isMatch(location, "**.*applicationcontext*.xml");
    }

    private static Boolean isServletContextLocation(String location, String flag) {
        location = location.toLowerCase();
        return ResourceUtils.isMatch(location, "*servletcontext-" + flag + "*.xml") || ResourceUtils.isMatch(location, "**/*servletcontext-" + flag + "*.xml") || ResourceUtils.isMatch(location, "**.*servletcontext-" + flag + "*.xml");
    }

    private static Boolean isPropertiesLocation(String location) {
        return StringUtils.endsWithIgnoreCase(location, PropertiesSuffix);
    }

    private static Boolean isXmlLocation(String location) {
        return StringUtils.endsWithIgnoreCase(location, ".xml");
    }

    private void processXmlConfigs(List<String> target, String location) {
        if (StringUtils.endsWithIgnoreCase(location, xmlRunModeSuffix)) {
            target.add(location);
        } else {
            for (String runMode : xmlRunModesSuffix) {
                if (StringUtils.endsWithIgnoreCase(location, runMode)) {
                    return;
                }
            }
            target.add(location);
        }
    }

    private void processPropertiesConfigs(Resource resource, Map<String, Resource> target) throws Exception {
        String path = resource.getURI().toString();
        String fileName = resource.getFilename();
        // trim file ext name
        path = path.substring(0, path.length() - PropertiesSuffix.length());
        if (StringUtils.endsWithIgnoreCase(fileName, propRunModeSuffix)) {
            target.put(path, resource);
        } else {
            for (String runMode : propRunModesSuffix) {
                if (StringUtils.endsWithIgnoreCase(fileName, runMode)) {
                    return;
                }
            }
            target.put(path, resource);
        }
    }

    public static Properties loadProperties(Resource resource) throws IOException {
        return loadProperties(resource.getInputStream());
    }

    public static Properties loadProperties(InputStream is) throws IOException {
        Properties prop = new Properties();
        loadProperties(prop, is);
        return prop;
    }

    public static void loadProperties(Properties prop, InputStream is) throws IOException {
        byte[] bytes = IOUtils.toByteArray(is);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        String csName = DetectEncoding.Instance.detect(bytes);
        prop.load(new InputStreamReader(bis, csName));
        is.close();
    }

    static Map<String, String> getServletInitParameters(ServletContext context) {
        Map<String, String> configMap = new LinkedHashMap<String, String>();
        if (context != null) {
            Enumeration<String> paramNames = context.getInitParameterNames();
            while (paramNames.hasMoreElements()) {
                String paramName = paramNames.nextElement();
                if (!StringUtils.endsWithIgnoreCase(paramName, ConfigLocationSuffix)) {
                    continue;
                }
                configMap.put(paramName, context.getInitParameter(paramName));
            }
        }
        return configMap;
    }

}
