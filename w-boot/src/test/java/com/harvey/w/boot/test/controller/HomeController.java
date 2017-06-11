package com.harvey.w.boot.test.controller;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.harvey.w.boot.test.model.SysUserModel;
import com.harvey.w.boot.test.service.SysUserService;

@Controller
public class HomeController {
    
    @Resource
    private SysUserService sysUserService;
    
    @RequestMapping("/")
    @ResponseBody
    public String home() {
        return "Hello World!";
    }
    
    @ResponseBody
    @RequestMapping("/user")
    public SysUserModel getUser(@RequestParam Integer userId) {
        return this.sysUserService.get(userId);
    }
}
