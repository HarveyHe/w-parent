package com.harvey.w.core.test.hibernate;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.springframework.test.context.ContextConfiguration;

import com.harvey.w.core.spring.ContextInitializer;
import com.harvey.w.core.test.service.BacTestService;

//@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(initializers = ContextInitializer.class)
public class ConAccessTest {

    @Resource
    private BacTestService bacTestService;

    //@Test
    public void testConAccess() throws Exception {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 5; i++) {
            final int times = i;
            executorService.execute(new Runnable() {
                
                @Override
                public void run() {
                    bacTestService.doConAccess(8, times);
                }
            });
        }
        Thread.sleep(1000 * 60L);
    }
}
