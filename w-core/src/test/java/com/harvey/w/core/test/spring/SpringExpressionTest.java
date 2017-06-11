package com.harvey.w.core.test.spring;

import com.harvey.w.core.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.harvey.w.core.bean.SpringExpressionBean;

public class SpringExpressionTest {

    private SpringExpressionBean seb;
    private Object value;

    @Before
    public void intial() {
        seb = new SpringExpressionBean(this);
    }

    @Test
    public void testGetValue() {
        value = "123";
        String expressionString = "'the value is:'+value";
        String actVal = seb.getValue(expressionString, String.class);
        Assert.assertEquals("the value is:123", actVal);
    }

    @Test
    public void testGetConfig() {
        String val = Config.get("sys.basePackage");
        String expressionString = "T(Config).get('sys.basePackage')";
        String actVal = seb.getValue(expressionString, String.class);
        Assert.assertEquals(val, actVal);
    }

    @Test
    public void testCalc() {
        String expressionString = "1 + 100/10 * 2 - 1";
        Integer nVal = seb.getValue(expressionString, Integer.class);
        Assert.assertEquals((Integer) 20, nVal);

        String expressionString2 = "1 == 1 && T(Context).getCurrentUser() == null";
        Boolean bVal = seb.getValue(expressionString2, Boolean.class);
        Assert.assertEquals(Boolean.TRUE, bVal);
    }

    public Object getValue() {
        return value;
    }
}
