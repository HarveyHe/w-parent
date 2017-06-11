package com.harvey.w.core.config.listener;

import java.lang.reflect.Method;

import com.harvey.w.core.config.Config;
import com.harvey.w.core.hibernate.SessionFactoryBeanProcessor;
import com.harvey.w.core.spring.BeanNameGenerator;
import com.harvey.w.core.spring.ConfigProperiesPlaceHolder;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigUtils;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.Ordered;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;

import com.harvey.w.core.aop.InterceptionConfigListener;
import com.harvey.w.core.utils.ResourceUtils;

public class CoreContextConfigListener implements ContextConfigListener, BeanFactoryPostProcessor, Ordered {

    private BeanNameGenerator beanNameGenerator = new BeanNameGenerator();

    @Override
    public void beforeStartup(final ConfigurableApplicationContext context) {
        final String[] basePackages = ResourceUtils.tokenizeToStringArray(Config.get("sys.basePackage"));
        if (basePackages != null && basePackages.length > 0) {
            if (context instanceof AnnotationConfigWebApplicationContext) {
                AnnotationConfigWebApplicationContext ctx = (AnnotationConfigWebApplicationContext) context;
                ctx.setBeanNameGenerator(beanNameGenerator);
                ctx.scan(basePackages);
            } else {
                boolean isScan = false;
                try {
                    Class<?> clazz = Class.forName("org.springframework.boot.context.embedded.AnnotationConfigEmbeddedWebApplicationContext");
                    if (context.getClass().isAssignableFrom(clazz)) {
                        Method method = clazz.getMethod("scan", basePackages.getClass());
                        method.invoke(context, new Object[] { basePackages });
                        isScan = true;
                        Method method1 = clazz.getMethod("setBeanNameGenerator", org.springframework.beans.factory.support.BeanNameGenerator.class);
                        method1.invoke(context, beanNameGenerator);
                    }
                } catch (Throwable throwable) {
                }
                if (!isScan) {
                    context.addBeanFactoryPostProcessor(new BeanFactoryPostProcessor() {

                        @Override
                        public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
                            ClassPathBeanDefinitionScanner scanner = new ClassPathBeanDefinitionScanner((BeanDefinitionRegistry) beanFactory, true, context.getEnvironment());
                            scanner.setBeanNameGenerator(beanNameGenerator);
                            scanner.scan(basePackages);
                        }
                    });
                }
            }
        }
        new InterceptionConfigListener().beforeStartup(context);
        context.addBeanFactoryPostProcessor(this);
    }

    @Override
    public void afterStartup(ConfigurableApplicationContext context) {

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        if (!beanFactory.containsBean(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR)) {
            beanFactory.registerSingleton(AnnotationConfigUtils.CONFIGURATION_BEAN_NAME_GENERATOR, beanNameGenerator);
        }
        BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;

        registry.registerBeanDefinition(ConfigProperiesPlaceHolder.class.getName(), BeanDefinitionBuilder.genericBeanDefinition(ConfigProperiesPlaceHolder.class).getBeanDefinition());
        registry.registerBeanDefinition(SessionFactoryBeanProcessor.class.getName(), BeanDefinitionBuilder.genericBeanDefinition(SessionFactoryBeanProcessor.class).getBeanDefinition());
    }

    @Override
    public int getOrder() {
        return 0;
    }

}
