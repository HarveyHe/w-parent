package com.harvey.w.core.test.hibernate;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.junit.Assert;

import com.harvey.w.core.context.Context;
import com.harvey.w.core.dao.utils.EntityUtils;
import com.harvey.w.core.exception.ConVersionException;
import com.harvey.w.core.test.model.BacTestModel;
import com.harvey.w.core.test.service.BacTestService;
import com.harvey.w.core.test.spring.ApplicationTests;

public class HibernateTest extends ApplicationTests {

    //@Test
    public void testSaveAll() throws Exception {
        BacTestService bacTestService = Context.getBean(BacTestService.class);
        BacTestModel model1 = new BacTestModel();
        model1.setBacName("model1");
        model1.setBacDesc("model1description");
        BacTestModel model2 = new BacTestModel();
        model2.setBacName("model2");
        model2.setBacDesc("model1description");
        List<BacTestModel> models = new ArrayList<BacTestModel>();
        models.add(model1);
        models.add(model2);
        bacTestService.saveAll(models);
    }

    // @Test
    public void testDataChangeListener() throws Exception {
        BacTestService bacTestService = Context.getBean(BacTestService.class);
        DataChangesEventListener listener = Context.getBean(DataChangesEventListener.class);
        BacTestModel model = new BacTestModel();
        model.setBacName("bac insert testing");
        model.setBacDesc("bac insert testing description");
        model = bacTestService.save(model);
        // for insert testing
        Assert.assertEquals(model.getClass(), listener.getModel().getClass());
        Assert.assertTrue(listener.getState().containsKey("bacName"));
        Assert.assertTrue(listener.getState().containsKey("bacDesc"));

        // for update testing
        model.setBacName("bac update testing");
        model = bacTestService.save(model);
        Assert.assertEquals(model.getClass(), listener.getModel().getClass());
        Assert.assertTrue(listener.getState().containsKey("bacName"));
        Assert.assertTrue(listener.getOldState().containsKey("bacName"));
    }

    //@Test
    public void testSysVersion() throws Exception {
        BacTestService bacTestService = Context.getBean(BacTestService.class);
        BacTestModel model = new BacTestModel();
        model.setBacName("test initial new sys version");
        model.setBacDesc("desc for test initial new sys version");
        model = bacTestService.save(model);
        // test inital new version
        Assert.assertEquals(Integer.valueOf(1), model.getSysVersion());

        {
            // test change sys version
            BacTestModel model1 = bacTestService.get(model.getBacId());
            model1.setBacName("changed sys version");
            model1 = bacTestService.save(model1);
            Assert.assertEquals(Integer.valueOf(2), model1.getSysVersion());
        }
        {
            // test concurrent version ,will throw ConVersionException
            try {
                model.setBacName("concurrent sys version");
                bacTestService.save(model);
                Assert.assertTrue("no any exception", false);
            } catch (Exception ex) {
                Assert.assertTrue(ex instanceof ConVersionException);
            }
        }
    }
    
    //@Test
    public void testDao() {
        SessionFactory sessionFactory = Context.getBean(SessionFactory.class);
        String name = EntityUtils.getIdFieldName(sessionFactory, BacTestModel.class);
        System.out.println(name);
    }
}
