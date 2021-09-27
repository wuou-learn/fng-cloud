package com.fng.enums;

/**
 * @Description
 * @Author wuou
 * @Date 2021/9/26 上午11:13
 * @Version 1.0.0
 */
public enum StringEnum {

    /**
     * jwt 密钥
     */
    SECRET("sajkd&d8as6&%^SIOOdmc;]"),

    /**
     * jwt签名是由谁生成的
     */
    ISSUSER("fng"),

    /**
     * 签名的主题
     */
    SUBJECT("fng-token"),

    /**
     * 观众
     */
    AUDIENCE("fng-app");

    private String name;

    StringEnum(String name) {
        this.name = name;
    }
}
