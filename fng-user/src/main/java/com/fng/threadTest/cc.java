package com.fng.threadTest;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/16 上午11:28
 * @Version 1.0.0
 */
public class cc {
    static int num = 0;
    static ReentrantLock lock = new ReentrantLock();
    static Condition condition1 = lock.newCondition();
    static Condition condition2 = lock.newCondition();
    public static void main(String[] args) {
        MyRunnable runnable = new MyRunnable();
        Thread thread1 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(num < 100){
                        lock.lock();
                        if(num % 2 == 0){
                            condition1.await();
                        }
                        num++;
                        System.out.println(Thread.currentThread().getName()+num);
                        System.out.println(Thread.currentThread().getName()+"等待");
                        System.out.println(Thread.currentThread().getName()+"唤醒2");
                        condition2.signal();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println(Thread.currentThread().getName()+"unlock");
                    lock.unlock();
                }
            }
        }, "Thread1-->");
        Thread thread2 = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while(num < 100){
                        lock.lock();
                        if(num % 2 != 0){
                            condition2.await();
                        }
                        num++;
                        System.out.println(Thread.currentThread().getName()+num);
                        System.out.println(Thread.currentThread().getName()+"等待");
                        System.out.println("唤醒1");
                        condition1.signal();
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    System.out.println(Thread.currentThread().getName()+"unlock");
                    lock.unlock();
                }
            }
        }, "Thread2-->");

        thread1.start();
        thread2.start();
    }

}
