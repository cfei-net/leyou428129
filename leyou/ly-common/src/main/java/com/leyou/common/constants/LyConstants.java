package com.leyou.common.constants;

/**
 * 把一些常量放到这个类管理起来
 */
public class LyConstants {

    // 图片服务器上传的目录
    public static final String IMAGE_PATH = "D:\\leyou-code\\428129\\software\\nginx-1.16.0\\html\\brand-logo";

    // 图片回显的url地址
    public static final String IMAGE_URL = "http://localhost/brand-logo/";

    /*注册时短信验证码在redis中的key的前缀*/
    public static final String USER_VERFYCODE_PRE = "user:verfycode:";

    /*所有微服务发起请求时携带的token存放的头信息的key*/
    public static final String APP_TOKEN_HEADER = "APP_TOKEN_HEADER";

    /*用户购物车对象在redis中的key的前缀*/
    public static final String CART_PRE = "ly:cart:uid:";
}
