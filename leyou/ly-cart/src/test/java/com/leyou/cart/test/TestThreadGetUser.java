package com.leyou.cart.test;

import com.leyou.common.auth.pojo.UserInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class TestThreadGetUser {

    private ThreadLocal<UserInfo> TL = new ThreadLocal();

    /**
     * 模拟多线程状态下的获取用户信息
     */
    public void test() {
        for (int i = 0; i< 5; i++){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String userId = RandomStringUtils.randomNumeric(8);
                    UserInfo userInfo = new UserInfo(Long.valueOf(userId), "小飞飞", "guest");
                    // 把当前用户存入map中
                    //userMap.put(Thread.currentThread(), userInfo);
                    TL.set(userInfo);

                    // 在当前线程中取多次用户信息
                    log.warn(" 第一次获取； 当前线程： {}  ====> 获取用户信息： {}", Thread.currentThread().getId(), TL.get());
                    log.warn(" 第二次获取； 当前线程： {}  ====> 获取用户信息： {}", Thread.currentThread().getId(), TL.get());

                }
            }).start();
        }
    }

    public static void main(String[] args) {
        new TestThreadGetUser().test();
    }

}
