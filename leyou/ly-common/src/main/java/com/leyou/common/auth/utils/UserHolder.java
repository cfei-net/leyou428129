package com.leyou.common.auth.utils;

import com.leyou.common.auth.pojo.UserInfo;

/**
 * 这个工具类： 把当前用户信息保存到当前线程中
 */
public class UserHolder {
    private static final ThreadLocal<UserInfo> TL = new ThreadLocal<>();

    public static void setUser(UserInfo user) {
        TL.set(user);
    }

    public static UserInfo getUser() {
        return TL.get();
    }

    public static void removeUser() {
        TL.remove();
    }
}