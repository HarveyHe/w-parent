package com.harvey.w.core.bean;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;

import com.harvey.w.core.config.Config;
import com.harvey.w.core.context.Context;

/**
 * SPEL 表达式
 * 如：T(Config).get('sys.basePackage')
 * 如：1 == 1 && T(Context).getCurrentUser() == null
 * 如：1 + 100/10 * 2 - 1
 */
public class SpringExpressionBean {
    private StandardEvaluationContext evaluationContext;
    private SpelExpressionParser expressionParser;
    private final Map<String, Expression> expressionCache = new ConcurrentHashMap<String, Expression>();

    public SpringExpressionBean() {
        this(null);
    }

    public SpringExpressionBean(Object rootObject) {
        StandardEvaluationContext evaluationContext = new StandardEvaluationContext(rootObject);
        StandardTypeLocator typeLocator = new StandardTypeLocator();
        typeLocator.registerImport(Config.class.getPackage().getName());
        typeLocator.registerImport(Context.class.getPackage().getName());
        evaluationContext.setTypeLocator(typeLocator);
        this.setEvaluationContext(evaluationContext);
    }

    public StandardEvaluationContext getEvaluationContext() {
        return evaluationContext;
    }

    public void setEvaluationContext(StandardEvaluationContext evaluationContext) {
        if (evaluationContext != null) {
            expressionCache.clear();
        }
        this.evaluationContext = evaluationContext;
    }

    public SpelExpressionParser getExpressionParser() {
        if (expressionParser == null) {
            expressionParser = new SpelExpressionParser();
        }
        return expressionParser;
    }

    public void setExpressionParser(SpelExpressionParser expressionParser) {
        this.expressionParser = expressionParser;
    }

    public Expression parseExpression(String expressionString) {
        Expression expression = this.expressionCache.get(expressionString);
        if (expression == null) {
            expression = this.getExpressionParser().parseExpression(expressionString);
            this.expressionCache.put(expressionString, expression);
        }
        return expression;
    }

    public <T> T getValue(String expressionString, Class<T> valueClass) {
        Expression expression = this.parseExpression(expressionString);
        return expression.getValue(evaluationContext, valueClass);
    }

    public <T> T getValue(String expressionString, Object rootObject, Class<T> valueClass) {
        Expression expression = this.parseExpression(expressionString);
        return expression.getValue(evaluationContext, rootObject, valueClass);
    }
    
    public static void main(String[] args) {
        SpringExpressionBean exp = new SpringExpressionBean();
        String val = exp.getValue("T(Config).get('sys.runEnv')", String.class);
        System.out.println(val);
    }

}
