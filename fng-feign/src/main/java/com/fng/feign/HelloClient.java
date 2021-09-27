package com.fng.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/3 上午11:37
 * @Version 1.0.0
 */
@Component
@FeignClient(value = "NACOS",path = "/api")
public interface HelloClient {

    @RequestMapping(value = "/hello",method = RequestMethod.GET)
    String hello();
}
