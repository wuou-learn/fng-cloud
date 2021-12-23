package com.fng.threadTest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/16 上午11:28
 * @Version 1.0.0
 */
public class vv {
    static int num = 0;
    static ReentrantLock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();

    public static void main(String[] args) {

        MyRunnable runnable = new MyRunnable();
        Thread thread1 = new Thread(runnable, "Thread1-->");
        Thread thread2 = new Thread(runnable, "Thread2-->");

        thread1.start();
        thread2.start();
    }

    static class MyRunnable implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    lock.lock();
                    condition.signal();
                    if (num < 5) {
                        num++;
                        System.out.println(Thread.currentThread().getName() + num);
                        condition.await();
                    } else {
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
    }
}
