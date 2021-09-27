package com.fng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/2 上午11:39
 * @Version 1.0.0
 */
@SpringBootApplication
@EnableDiscoveryClient
public class FngNacosStarter {
    public static void main(String[] args) {
        SpringApplication.run(FngNacosStarter.class,args);
    }
}
