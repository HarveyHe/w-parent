package com.harvey.w.core.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.apache.bcel.classfile.ClassFormatException;
import org.aspectj.apache.bcel.classfile.ClassParser;
import org.aspectj.apache.bcel.classfile.JavaClass;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.ContextResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

public final class ResourceUtils {
    private final static Log log = LogFactory.getLog(ResourceUtils.class);
    private final static PathMatchingResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();

    // private final static MetadataReaderFactory metadataReaderFactory = new
    // CachingMetadataReaderFactory();

    public static List<Resource> getResources(String pattern) throws IOException {
        return Arrays.asList(resourcePatternResolver.getResources(pattern));
    }

    public static Resource getResource(String pattern) {
        return resourcePatternResolver.getResource(pattern);
    }

    public static List<Resource> getResources(String basePackages, String pattern) {
        if (pattern == null || pattern.length() == 0) {
            pattern = "**.*";
        } else if (!pattern.contains("**")) {
            pattern = "/**/" + pattern;
        }
        if (basePackages == null) {
            basePackages = "";
        }
        String[] basePackagesArr = tokenizeToStringArray(basePackages);
        List<Resource> resources = new ArrayList<>();
        for (String basePackage : basePackagesArr) {
            String searchPath = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX + ClassUtils.convertClassNameToResourcePath(basePackage) + pattern;
            try {
                Resource[] reses = resourcePatternResolver.getResources(searchPath);
                resources.addAll(Arrays.asList(reses));
            } catch (Exception e) {
                log.error(e);
            }
        }
        return resources;
    }

    public static List<Class<?>> getClasses(String basePackages, String pattern) {
        List<Class<?>> classes = new ArrayList<Class<?>>();
        if (pattern == null || pattern.length() == 0) {
            pattern = "*.class";
        } else if (!pattern.endsWith(".class")) {
            pattern = pattern + ".class";
        }
        try {
            List<Resource> resources = getResources(basePackages, pattern);
            for (Resource resource : resources) {
                // MetadataReader metadataReader =
                // metadataReaderFactory.getMetadataReader(resource);
                // String className =
                // metadataReader.getClassMetadata().getClassName();
                JavaClass javaClass = getJavaClass(resource.getInputStream(), resource.getFilename());
                Class<?> clazz = Class.forName(javaClass.getClassName());
                classes.add(clazz);
            }
        } catch (Exception e) {
            log.error(e);
        }
        return classes;
    }

    public static String getResourcePath(Resource resource) throws Exception {
        if (resource instanceof ContextResource) {
            return '/' + ((ContextResource) resource).getPathWithinContext();
        }
        return resource.getURL().toString();
    }

    public static Boolean isPattern(String pattern) {
        return resourcePatternResolver.getPathMatcher().isPattern(pattern);
    }

    public static Boolean isMatch(String path, String pattern) {
        return resourcePatternResolver.getPathMatcher().match(pattern, path);
    }

    public static Boolean isMatchStart(String path, String pattern) {
        return resourcePatternResolver.getPathMatcher().matchStart(pattern, path);
    }

    public static String[] tokenizeToStringArray(String pattern) {
        if (StringUtils.isEmpty(pattern)) {
            return new String[0];
        }
        return StringUtils.tokenizeToStringArray(pattern, ConfigurableApplicationContext.CONFIG_LOCATION_DELIMITERS);
    }

    public static JavaClass getJavaClass(InputStream is, String fileName) throws ClassFormatException, IOException {
        ClassParser parser = new ClassParser(is, fileName);
        return parser.parse();
    }

}
