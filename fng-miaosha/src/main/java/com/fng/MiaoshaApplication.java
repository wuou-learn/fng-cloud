package com.fng;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/27 上午11:53
 * @Version 1.0.0
 */
@SpringBootApplication
public class MiaoshaApplication extends SpringBootServletInitializer {

    /**
     * 重写Spring boot启动配置绑定
     * @param application
     * @return
     */
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(MiaoshaApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(MiaoshaApplication.class,args);
    }
}
