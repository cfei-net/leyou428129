package com.leyou.user.controller;

import com.leyou.common.constants.LyConstants;
import com.leyou.common.exception.LyException;
import com.leyou.user.dto.UserDTO;
import com.leyou.user.entity.User;
import com.leyou.user.service.UserService;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.stream.Collectors;

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
    @ApiOperation(value = "校验用户名数据是否可用")
    @ApiResponses({
            @ApiResponse(code = 200, message = "校验结果有效，true或false代表可用或不可用"),
            @ApiResponse(code = 400, message = "请求参数有误，比如type不是指定值")
    })
    public ResponseEntity<Boolean> checkData(
            @ApiParam(value = "要校验的数据", example = "xiaofeifei")
            @PathVariable("data") String data,
            @ApiParam(value = "数据类型，1：用户名，2：手机号", example = "1")
            @PathVariable("type") Integer type)
    {
        Boolean flag = userService.checkData(data, type);
        return ResponseEntity.ok(flag);
    }

    /**
     * 发送短信验证码
     * @param phone 手机号码
     * @return
     */
    @ApiOperation(value = "发送短信验证码")
    @PostMapping("/code")
    public ResponseEntity<Void> sendSms(@RequestParam("phone") String phone){
        userService.sendSms(phone);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 用户注册接口
     * @param user  用户接参
     * @param code  短信验证码
     * @return      无
     */
    @ApiOperation(value = "用户注册接口")
    @PostMapping("/register")
    public ResponseEntity<Void> register(@Valid User user, BindingResult bindingResult, @RequestParam("code") String code){
        // 手动从user中取出值来进行判空等校验： 服务器端校验
        if(bindingResult.hasErrors()){
            // 如果有异常，收集异常信息
            String errors = bindingResult.getAllErrors()
                    .stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining("|"));
            throw new LyException(errors, 400);
        }

        userService.register(user, code);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Autowired
    private HttpServletRequest request;

    /**
     * 根据用户名和密码查询用户
     * @param username  用户名
     * @param password  密码
     * @return          用户的DTO对象
     */
    @ApiOperation(value = "根据用户名和密码查询用户")
    @GetMapping("/query")
    public ResponseEntity<UserDTO> queryUserByUsernameAndPwd(
            @RequestParam("username") String username,
            @RequestParam("password") String password
    ){
        System.out.println("=========>"+request.getHeader(LyConstants.APP_TOKEN_HEADER));
        UserDTO userDTO = userService.queryUserByUsernameAndPwd(username,password);
        return ResponseEntity.ok(userDTO);
    }
}
