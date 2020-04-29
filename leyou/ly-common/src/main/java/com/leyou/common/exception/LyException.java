package com.leyou.common.exception;

import com.leyou.common.exception.enums.ExceptionEnum;
import lombok.Getter;

/**
 * 自定义乐优商城的异常
 */
public class LyException extends RuntimeException {

    @Getter
    private Integer status; // 状态码

    public LyException(String message, Integer status) {
        super(message);
        this.status = status;
    }

    // 新增构造器： 传入枚举对象
    public LyException(ExceptionEnum exceptionEnum) {
        super(exceptionEnum.getMessage());
        this.status = exceptionEnum.getStatus();
    }
}
