package com.fng.threadTest;

/**
 * @Description
 * @Author wuou
 * @Date 2021/12/15 下午4:27
 * @Version 1.0.0
 */
public class ll {
    private static int num = 0;
    private static Object lock = new Object();

    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Thread thread1 = new Thread(myRunnable, "Thread1-->");
        Thread thread2 = new Thread(myRunnable, "Thread2-->");
        thread1.start();
        thread2.start();
    }

    static class MyRunnable implements Runnable {

        @Override
        public void run() {
            while (true){
                synchronized (this) {
                    notify();
                    if (num < 100) {
                        num++;
                        System.out.println(Thread.currentThread().getName() + num);
                        try {
                            wait();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else {
                        break;
                    }
                }
            }
        }
    }
}
