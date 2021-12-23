package com.fng.common;

public interface LoadBalance {
    /**
     * 根据某种策略获取相关服务
     * @return
     */
    LoadBService doPickOneService();
}