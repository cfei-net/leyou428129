package com.leyou.common.exception.controller;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.pojo.ExceptionResult;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 全局异常处理
 */
@ControllerAdvice
public class LyExceptionController {

    /**
     * 注解：不具备什么的功能，它只是启到了标记的作用
     * 全局异常的通知
     *
     * 返回值：我们不能返回LyException，我们返回的信息应该单独封装一个类
     *
     * @param e
     * @return
     */
    @ExceptionHandler(LyException.class) //这个注解告诉springmvc处理什么样的异常
    public ResponseEntity<ExceptionResult> handlerLyException(LyException e){
        return ResponseEntity.status(e.getStatus()).body(new ExceptionResult(e));
    }
}
