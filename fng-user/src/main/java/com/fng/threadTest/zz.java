package com.fng.threadTest;

import lombok.SneakyThrows;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/15 下午4:27
 * @Version 1.0.0
 */
public class zz {
    private static int num = 0;
    private static volatile boolean flag = true;
    private static Object lock = new Object();
    public static void main(String[] args) {
        Thread thread1 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while(true){
                    synchronized (lock){
                        if(flag && num < 100){
                            num ++;
                            System.out.println(Thread.currentThread().getName()+num);
                            flag = false;
                        }else {
                            break;
                        }
                    }
                }


            }
        },"Thread1-->");

        Thread thread2 = new Thread(new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while(true){
                    synchronized (lock){
                        if(!flag && num < 100){
                            num ++;
                            System.out.println(Thread.currentThread().getName()+num);
                            flag = false;
                        }else {
                            break;
                        }
                    }
                }
            }
        },"Thread2-->");
        thread1.start();
        thread2.start();
    }

}
