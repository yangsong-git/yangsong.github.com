package com.volatiledemo;

import java.util.concurrent.TimeUnit;

/**
 * 1，验证volatile的可见性
 * 1.1 假如int number = 0，number前面没有添加volatile关键字修饰，
 *     那么修改number后，后面的线程不能即使得到通知，即没有可见性
 * 1.2 添加了volatile之后，number值改变，其余线程能即使得到number值被修改的通知，
 *     即解决了没有可见性的问题
 *
 * 2 验证volatile不保证原子性
 * 2.1 原子性是什么意思？
 *     不可能分割，完整性，也即某个线程正在执行某个业务时，不可被加塞和分割，需要整体完整一直性，
 *     直至线程操作完成，要么一起成功，要么一起失败
 */
public class VolatileDemo {
    public static void main(String[] args) {
        noAtomicity();
    }
    /**
     *功能描述：验证volatile不保证原子性
     * @author yangsong
     * @date 2020-05-23 22:52
     * @param
     * @return void
     */
    private static void noAtomicity() {
        MyData myData = new MyData();

        for (int i = 0; i < 20; i++) {
            new Thread(()->{
                for (int j = 0; j < 1000; j++) {
                    myData.addPlusPlus();
                }

            },String.valueOf(i)).start();
        }
        //activeCount此方法是统计线程数，后台默认有两个线程数，1-是main线程，2-是GC线程，
        //线程数大于2就说明还有线程在计算，yield方法就是礼让线程，等线程计算完之后在执行
        while (Thread.activeCount()>2){
            Thread.yield();
        }

        System.out.println(Thread.currentThread().getName()+"号线程执行number值为："+myData.number);
    }

    /**
     *功能描述 volatile可见性
     * @author yangsong
     * @date 2020-05-23 18:27
     * @param
     * @return void
     */
    private static void seeOkByVolatile() {
        MyData myData = new MyData();

        new Thread(()->{
            System.out.println(Thread.currentThread().getName()+"===第一次进入此线程");
            try {
                //此处线程睡眠5秒，5秒后继续向下执行代码
                TimeUnit.SECONDS.sleep(5);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
            //调用方法，修改number值
            myData.updateNumber();
            //修改成功之后执行
            System.out.println(Thread.currentThread().getName()+"====此处number被修改"+myData.number);
        },"第一个线程").start();

        //验证number值是否被修改的线程
        while (myData.number==0){
            //如果没有volatile可见性，则一直在此循环
        }
        System.out.println(Thread.currentThread().getName()+"====此处number="+myData.number+"值已被改变");
    }
}

class MyData{
    /**
     * 此处如果不加volatile，第一个线程修改number值之后，不会通知第二个线程，导致第二个线程死循环
     * 此处加了volatile关键字，number被修改后会通知到第二个线程，值已被修改，此处就是volatile的可见性
     */
    volatile int number = 0;

    public void updateNumber(){
        this.number=20;
    }

    public void addPlusPlus(){
        number++;
    }
}