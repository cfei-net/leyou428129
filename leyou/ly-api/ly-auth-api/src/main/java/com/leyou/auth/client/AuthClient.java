package com.leyou.auth.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("auth-service")
public interface AuthClient {

    /**
     * 生成token返回给每个微服务
     * @param id        ： 服务id
     * @param password  ： 微服务自己的密码
     * @return          ： 返回JWT
     */
    @GetMapping("/authorization")
    public String authorization(@RequestParam("id") Long id,
                                @RequestParam("secret") String password
    );
}
