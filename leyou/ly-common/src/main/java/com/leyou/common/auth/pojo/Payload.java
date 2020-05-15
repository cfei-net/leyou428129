package com.leyou.common.auth.pojo;

import lombok.Data;

import java.util.Date;

/**
 * JWT中的载荷对象： 中间部分
 */
@Data
public class Payload<T> {
    private String id;
    private T userInfo;
    private Date expiration;
}