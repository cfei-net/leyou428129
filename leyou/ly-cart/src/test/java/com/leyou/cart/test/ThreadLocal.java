package com.leyou.cart.test;

import com.leyou.common.auth.pojo.UserInfo;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 我们工具类:  有可能导致内存泄露：
 *          1）因为线程请求执行完，线程就销毁了，
 *          2）value是一个对象，是对象，就会在jvm的堆中开辟空间，分配了内存
 *          3）存储是一个map： key销毁，但是value还有，还有指针指向堆内存，GC无法回收堆中的对象，有可能发生内存泄露
 *
 * 使用JDK的ThreadLocal就不会出现这个问题：jdk已经想到了这个问题
 */
/*public class ThreadLocal<U> {
    // map
    private ConcurrentHashMap<Thread, U> userMap = new ConcurrentHashMap<>();

    public void set(U userInfo){
        userMap.put(Thread.currentThread(), userInfo);
    }

    public U get(){
        return userMap.get(Thread.currentThread());
    }

    public void removeUser(){
        userMap.remove(Thread.currentThread());
    }
}*/
