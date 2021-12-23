package com.fng.threadTest;

//一个线程打印1 2 3 ...
//一个线程打印a b c ...
//一个线程打印A B C ...
//交替打印 1 a A 2 b B 3 c C ... 直到所有字母打印完毕
public class Thread4 {
    private static int index = 1;
    private static volatile int f = 0;
    
    public static void main(String[] args){

        new Thread(){
            @Override
            public void run() {
                while(index<=26){
                    if(f==0){
                        System.out.println(index++);
                        f = 1;
                    }
                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                while(index<=27){
                    if(f==1){
                        System.out.println((char)('a'+index-2));
                        f = 2;
                        if(index==27)break;
                    }

                }
            }
        }.start();


        new Thread(){
            @Override
            public void run() {
                while(index<=27){
                    if(f==2){
                        System.out.println((char)('A'+index-2));
                        f = 0;
                        if(index==27)break;
                    }
                }
            }
        }.start();

    }
}

