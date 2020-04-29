package com.leyou.item.service;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ItemService {

    @Transactional(rollbackFor = RuntimeException.class) // Spring处理事务的时候默认的异常类型
    public Long saveItem(Long id){
        // 模拟添加操作，如果id为1抛异常
        if(id.equals(1L)){
            // 异常信息没有统一： 异常的信息很乱，而且返回的状态码得不到一个统一的规范，学习一下SpringMVC：HttpStatus枚举类
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        return id;
    }
}