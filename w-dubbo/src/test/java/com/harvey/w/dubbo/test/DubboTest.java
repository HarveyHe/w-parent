package com.harvey.w.dubbo.test;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;

import com.harvey.w.core.spring.ContextConfigInitializer;
import com.harvey.w.dubbo.test.hello.entity.HelloEntity;
import com.harvey.w.dubbo.test.hello.service.HelloService;

public class DubboTest {
    private ConfigurableApplicationContext context;

    @Before
    public void startup() {
        context = ContextConfigInitializer.createApplicationContext();
        context.refresh();
    }

    @Test
    public void testDubbo() {
        HelloService helloService = context.getBean("helloService",HelloService.class);
        HelloEntity entity = new HelloEntity();
        entity.setDate(new Date());
        entity.setIsTrue(false);
        entity.setWord("I'am from Client");
        String result = helloService.saveHello(entity);
        System.out.println(result);

        entity = helloService.getHello();
        System.out.println(entity);
    }

    @After
    public void shutdown() {
        context.stop();
        context.close();
    }
}
