package com.fng.threadTest;

import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/15 下午4:27
 * @Version 1.0.0
 */
public class xx {
    private static AtomicInteger num = new AtomicInteger(0);
    private static volatile int flag = 0;
    private static Object lock = new Object();

    public static void main(String[] args) {
        Thread thread1 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (num.get() < 100) {
                    if (flag == 0) {
                        num.addAndGet(1);
                        System.out.println(Thread.currentThread().getName() + num);
                        flag = 1;
                    }
                }


            }
        }, "Thread1-->");

        Thread thread2 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (num.get() < 100) {
                    if (flag == 1) {
                        num.addAndGet(1);
                        System.out.println(Thread.currentThread().getName() + num);
                        flag = 2;
                    }
                }
            }
        }, "Thread2-->");

        Thread thread3 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (num.get() < 100) {
                    if (flag == 2) {
                        num.addAndGet(1);
                        System.out.println(Thread.currentThread().getName() + num);
                        flag = 0;
                    }
                }
            }
        }, "Thread3-->");
        thread1.start();
        thread2.start();
        thread3.start();
    }

}
