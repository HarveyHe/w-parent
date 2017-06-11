package com.harvey.w.core.spring.rest;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ValueConstants;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import com.harvey.w.core.utils.ReflectionUtils;

/**
 * 自适应 RequestParam注解
 * @author admin
 *
 */
public class RestRequestParamArgumentResolver extends RequestParamMethodArgumentResolver {

    public RestRequestParamArgumentResolver(ConfigurableBeanFactory beanFactory, boolean useDefaultResolution) {
        super(beanFactory, useDefaultResolution);
    }


    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        if (!parameter.hasParameterAnnotation(PathVariable.class) && BeanUtils.isSimpleProperty(parameter.getParameterType())) {
            return true;
        }
        return super.supportsParameter(parameter);
    }


    @Override
    protected NamedValueInfo createNamedValueInfo(MethodParameter parameter) {
        NamedValueInfo namedValueInfo = null;
        String[] names = ReflectionUtils.getMethodParameterNames(parameter.getMethod());
        if(names.length > 0 && parameter.getParameterIndex() > -1 && parameter.getParameterIndex() < names.length){
            String name = names[parameter.getParameterIndex()];
            if(parameter.getParameterType().isArray()){
                name += "[]";
            }
            namedValueInfo = new NamedValueInfo(name, false, ValueConstants.DEFAULT_NONE);
        }else{
            namedValueInfo = super.createNamedValueInfo(parameter);
        }
        return namedValueInfo;
    }

}
