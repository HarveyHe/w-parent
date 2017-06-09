package com.harvey.w.gateway.demo.controller;

import com.harvey.w.gateway.common.entity.CommonResponse;
import com.harvey.w.gateway.common.entity.Message;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author harvey
 */
@RestController
@RequestMapping("/demo")
public class DemoController {
    @RequestMapping("/testDemo")
    public CommonResponse<String> testDemo() {
        CommonResponse<String> responseMessage = new CommonResponse<String>();
        return responseMessage;
    }

    @RequestMapping("query")
    public CommonResponse<List<String>> queryOrders(@RequestBody Message<String> request){
        CommonResponse<List<String>> responseMessage = new CommonResponse<List<String>>();
        List<String> entities = new ArrayList<String>();
        for (int i = 0; i < 10;i++) {
            entities.add(i +  "");
        }
        responseMessage.setData(entities);
        return responseMessage;
    }

    @RequestMapping("/{id}")
    public CommonResponse<String> get(@PathVariable("id") String id){
        CommonResponse<String> responseMessage = new CommonResponse<String>();
        String entity = "test";
        responseMessage.setData(entity);
        return responseMessage;
    }
}
