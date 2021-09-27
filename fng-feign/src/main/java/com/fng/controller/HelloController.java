package com.fng.controller;

import com.fng.feign.HelloClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/3 上午11:38
 * @Version 1.0.0
 */
@RestController
public class HelloController {

    @Autowired
    private HelloClient helloClient;

    @RequestMapping(value = "/feign", method = RequestMethod.GET)
    public String feign(){
        String hello = helloClient.hello();

        return "----->"+hello;
    }
}
