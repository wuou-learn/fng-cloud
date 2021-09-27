package com.fng.controller;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/3 上午11:17
 * @Version 1.0.0
 */
@RestController
@RequestMapping("/api")
@RefreshScope
public class HelloController {

    @Autowired
    private StringEncryptor encryptor;

    @Value("${api.hello}")
    private String str;

    @Value("${server.port}")
    private String port;

    @RequestMapping(value = "/hello", method = RequestMethod.GET)
    public String hello(){
        System.out.println(encryptor.encrypt("hello world!"));
        System.out.println(encryptor.encrypt("8001"));
        System.out.println(encryptor.decrypt(encryptor.encrypt("sadadsa")));
        return str+port;
    }

}
