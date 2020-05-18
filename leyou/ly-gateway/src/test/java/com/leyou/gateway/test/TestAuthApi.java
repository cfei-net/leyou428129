package com.leyou.gateway.test;

import com.leyou.auth.client.AuthClient;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class TestAuthApi {

    @Autowired
    private AuthClient authClient;

    @Test
    public void test(){
        // 数据库微服务的密码默认与服务名称一致
        String token = authClient.authorization(7L, "api-gateway");
        log.warn(token);
    }
}
