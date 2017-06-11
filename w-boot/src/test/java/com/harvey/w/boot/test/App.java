package com.harvey.w.boot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;

@EnableAutoConfiguration
public class App {

    public static void main(String[] args) throws Exception {
        SpringApplication app = new SpringApplication(App.class);
        app.run(args);
    }
}
