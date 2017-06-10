package com.harvey.w.demo.service.impl;

import com.gsst.eaf.core.service.impl.BaseServiceImpl;
import com.harvey.w.demo.service.DemoService;
import org.springframework.stereotype.Service;

/**
 * @author harvey
 */
@Service
public class DemoServiceImpl extends BaseServiceImpl implements DemoService {

    @Override
    public String helloWorld() {
        return "Hello World, Will test!";
    }
}
