package com.fng.threadTest;

//Main.java
class MyRunnable implements Runnable {
    private int number = 1;

    @Override
    public void run() {
        while (true) {
            synchronized (this) {
                notify();
                if (number <= 100) {
                    System.out.println(Thread.currentThread().getName() + ": " + number);
                    number++;
                    try {
                        wait(); //wait()阻塞并释放锁
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    break;
                }
            }
        }
    }
}

public class Main {
    public static void main(String[] args) {
        MyRunnable myRunnable = new MyRunnable();
        Thread t1 = new Thread(myRunnable);
        Thread t2 = new Thread(myRunnable);
        t1.start();
        t2.start();
    }
}
