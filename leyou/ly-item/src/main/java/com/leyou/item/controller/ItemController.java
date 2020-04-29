package com.leyou.item.controller;

import com.leyou.common.exception.LyException;
import com.leyou.item.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ItemController {
    @Autowired
    private ItemService itemService;

    /**
     * 问题：
     *      1、我们接口能返回数据，但是状态码不能返回，很多的前端他来调用的我们接口，是根据状态码来定义是否成功
     *      2、现在我们在测试接口的时候发现：有一个状态码：这个状态码其实是SpringMVC返回给我们的，不是我们定义的
     * 返回状态码给前端：
     *      SpringMVC给我们提供了类来指定返回的状态码和返回的内容： ResponseEntity<泛型指定的是返回的内容>
     * 状态码的枚举类：
     *      HttpStatus： 提供好了很多常用的状态码
     *          CREATED： 新增
     *          NO_CONTENT： 修改和删除
     *          OK ： 查询
     * 异常处理：我们自定义了异常，异常中也有状态码，但是SpringMVC返回给前端的时候，没有把我们的状态码返回
     * 怎么解决？
     * 	1）使用Spring的AOP
     * 	2）SpringMVC提供了统一的异常处理机制：ExceptionHandler
     *
     *
     */
    @PostMapping("/save")
    public ResponseEntity<Object> saveItem(@RequestParam("id") Long id){
        //try {   // 使用SpringMVC处理统一异常
            // 自己指定状态码
        /*return ResponseEntity.status(HttpStatus.OK) // 返回的状态码
                .body(itemService.saveItem(id));  // 返回的内容*/



            // 如果是查询请求，一般都是OK
            return ResponseEntity.ok(itemService.saveItem(id));



        /*}catch (LyException e){
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }*/
    }

}