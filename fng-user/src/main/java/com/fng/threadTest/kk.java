package com.fng.threadTest;

import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/15 下午4:27
 * @Version 1.0.0
 */
public class kk {
//    private static volatile AtomicInteger num = new AtomicInteger(0);
    private static volatile AtomicInteger num = new AtomicInteger(0);
    private static Object lock = new Object();
    public static void main(String[] args) {
        Myrunnable myrunnable = new Myrunnable();
        Thread thread1 = new Thread(myrunnable,"Thread1-->");

        Thread thread2 = new Thread(myrunnable,"Thread2-->");
        thread1.start();
        thread2.start();
    }

    static class Myrunnable implements Runnable {

        @SneakyThrows
        @Override
        public void run() {
            while(true){
                synchronized (lock){
                    lock.notify();
                    if(num.get() < 100){
                        num.addAndGet(1);
                        System.out.println(Thread.currentThread().getName()+num.get());
                        lock.wait();
                    }else {
                        break;
                    }
                }
            }
        }
    }
}
