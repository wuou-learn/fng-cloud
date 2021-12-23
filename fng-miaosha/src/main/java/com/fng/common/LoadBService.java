package com.fng.common;

import java.util.concurrent.atomic.AtomicInteger;

public class LoadBService {
    /**
     * 服务名称
     */
    private String name;

    private String key;

    private volatile AtomicInteger sum = new AtomicInteger(100);

    private volatile boolean available;

    public LoadBService(String name, String key) {
        this.name = name;
        this.key = key;
        this.available = true;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    public AtomicInteger getSum() {
        return sum;
    }

    @Override
    public String toString() {
        return "LoadBService{" +
                "name='" + name + '\'' +
                ", key='" + key + '\'' +
                ", available=" + available +
                '}';
    }
}