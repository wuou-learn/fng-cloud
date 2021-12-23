package com.fng.threadTest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock 实现源码学习
 * @author 一枝花算不算浪漫
 * @date 2020/4/28 7:20
 */
public class ReentrantLockDemo {
    static ReentrantLock lock = new ReentrantLock();
    static int num = 0;
    public static void main(String[] args) {
        Condition condition1 = lock.newCondition();
        Condition condition2 = lock.newCondition();

        new Thread(() -> {
            lock.lock();
            try {
                condition2.signal();
                while(num < 5){
                    System.out.println("线程一加锁成功");
                    num ++;
                    System.out.println(Thread.currentThread().getName()+num);
                    System.out.println("线程一执行await被挂起");
                    condition1.await();

                    System.out.println("唤醒线程二");
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("线程一释放锁成功");
            }
        },"Thread1--->").start();

        new Thread(() -> {
            lock.lock();
            try {
                condition1.signal();
                while (num < 5){
                    System.out.println("线程二加锁成功");
                    num++;
                    System.out.println(Thread.currentThread().getName()+num);
                    System.out.println("线程二执行await被挂起");
                    condition2.await();

                    System.out.println("唤醒线程一");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
                System.out.println("线程二释放锁成功");
            }
        },"Thread2--->").start();
    }
}
