package com.fng.executor;

/**
 * Task任务实现类
 */
public class SendMessageRunnable implements Runnable {

    // 等待发送消息
    private String msg;

    // 私有构造方法
    private SendMessageRunnable(String msg) {
        msg = this.msg;
    }

    @Override
    public void run() {
        // TODO 发送日志消息
    }

}