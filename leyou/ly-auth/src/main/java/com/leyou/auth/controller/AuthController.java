package com.leyou.auth.controller;

import com.leyou.auth.service.AuthService;
import com.leyou.common.auth.pojo.UserInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
public class AuthController {

    @Autowired
    private AuthService authService;

    /**
     * 用户登录请求
     * @param username      用户名
     * @param password      登录密码
     * @param request       请求对象： CookieUtils使用
     * @param response      响应对象： CookieUtils使用
     * @return              没有返回值：但是工具类把jwt写入了浏览器的cookie中
     */
    @PostMapping("/login")
    public ResponseEntity<Void> login(@RequestParam("username") String username,
                                      @RequestParam("password") String password,
                                      HttpServletRequest request,
                                      HttpServletResponse response){
        authService.login(username, password, request, response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 验证用户
     * @param request       请求对象
     * @return              JWT的载荷对象
     */
    @GetMapping("/verify")
    public ResponseEntity<UserInfo> verify(HttpServletRequest request,HttpServletResponse response){
        UserInfo userInfo = authService.verify(request,response);
        return ResponseEntity.ok(userInfo);
    }

    /**
     * 用户退出
     * @param request
     * @param response
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request,HttpServletResponse response){
        authService.logout(request,response);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 生成token返回给每个微服务
     * @param id        ： 服务id
     * @param password  ： 微服务自己的密码
     * @return          ： 返回JWT
     */
    @GetMapping("/authorization")
    public ResponseEntity<String> authorization(@RequestParam("id") Long id,
                                                @RequestParam("secret") String password
                                                ){
        String token = authService.authorize(id, password);
        return ResponseEntity.ok(token);
    }
}
