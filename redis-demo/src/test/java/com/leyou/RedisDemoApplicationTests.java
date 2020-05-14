package com.leyou;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
class RedisDemoApplicationTests {

    /**
     * Redis：
     *      1）这个模板默认采用Jdk的序列化方式把数据存入redis： ObjectOutSteam【不推荐】 RedisTemplate redisTemplate;
     *      2）推荐StringRedisTemplate： 这个模板操作的都是字符串，比较简单【推荐】
     */
    @Autowired
    private StringRedisTemplate redisTemplate;

    @Test
    void contextLoads() {
        // 保存
        redisTemplate.opsForValue().set("name","xiaofeifei");
        // 获取值
        Object name = redisTemplate.opsForValue().get("name");
        System.out.println(name);
    }

    @Test
    public void test(){
        redisTemplate.delete("name");
    }

}
