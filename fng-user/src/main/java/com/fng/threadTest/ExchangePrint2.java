package com.fng.threadTest;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ExchangePrint2 {


    public static void main(String[] args) throws InterruptedException {
        ReentrantLock rLock = new ReentrantLock();
        Condition condition = rLock.newCondition();
        Condition condition1 = rLock.newCondition();

        new Thread(() -> {
            char a = 'A';
            try {
                for (int i = 0; i < 26; i++) {
                    rLock.lock();
                    System.out.println(a);
                    a++;
                    condition1.signal();
                    condition.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rLock.tryLock();
            }
        }).start();
        new Thread(() -> {
            try {
                for (int i = 1; i < 27; i++) {
                    rLock.lock();
                    System.out.println(i);
                    condition.signal();
                    condition1.await();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                rLock.tryLock();
            }
        }).start();


    }
}