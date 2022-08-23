package com.fng;

import com.fng.config.CouponTopicProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 上午11:53
 * @Version 1.0.0
 */
@SpringBootApplication
public class SingleApplication {
    public static void main(String[] args) {
        System.out.println("启动前--------------------->"+ CouponTopicProperties.TOPIC);
        SpringApplication.run(SingleApplication.class,args);
        System.out.println("启动后--------------------->"+ CouponTopicProperties.TOPIC);
    }
}
