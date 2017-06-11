package com.harvey.w.core.test.support;

import com.harvey.w.core.reflectasm.MethodAccess;

public class Test {

    public static void main(String[] args) {
        MethodAccess.get(Test.class).invoke(new Test(), "test", "abc","123");
    }
    
    public String test(String a,String b) {
        System.out.println(String.format("args:%s-%s", a,b));
        return "ok";
    }
}
