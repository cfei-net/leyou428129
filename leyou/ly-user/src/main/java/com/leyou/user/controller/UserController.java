package com.leyou.user.controller;

import com.leyou.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class UserController {

    @Autowired
    private UserService userService;


    /**
     * 实现用户数据的校验，主要包括对：手机号、用户名的唯一性校验。
     * @param data  要校验的数据
     * @param type  要校验的数据类型：1，用户名；2，手机
     * @return
     */
    @GetMapping("/check/{data}/{type}")
    public ResponseEntity<Boolean> checkData(@PathVariable("data") String data,
                                             @PathVariable("type") Integer type){
        Boolean flag = userService.checkData(data, type);
        return ResponseEntity.ok(flag);
    }

    /**
     * 发送短信验证码
     * @param phone 手机号码
     * @return
     */
    @PostMapping("/code")
    public ResponseEntity<Void> sendSms(@RequestParam("phone") String phone){
        userService.sendSms(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
