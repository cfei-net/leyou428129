package com.leyou.page.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSendMsg {


    //private RabbitTemplate rabbitTemplate;

    @Autowired
    private AmqpTemplate amqpTemplate;


    @Test
    public void test() throws InterruptedException {
        amqpTemplate.convertAndSend("feifei.exchange", "aaaa.bbbb", "xiaofeifei so pure!");
        Thread.sleep(10000);
    }
}
