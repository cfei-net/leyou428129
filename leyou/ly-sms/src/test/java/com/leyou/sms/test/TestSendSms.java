package com.leyou.sms.test;

import com.leyou.LySmsApplication;
import com.leyou.common.constants.MQConstants;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.HashMap;
import java.util.Map;

import static com.leyou.common.constants.MQConstants.Exchange.SMS_EXCHANGE_NAME;
import static com.leyou.common.constants.MQConstants.RoutingKey.VERIFY_CODE_KEY;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestSendSms {

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Test
    public void sendSms() {
        String code = RandomStringUtils.randomNumeric(6);
        System.out.println("code====>"+code);

        Map<String, String> msg = new HashMap();
        msg.put("phone", "13106970182");
        msg.put("code", code);

        // 发送
        amqpTemplate.convertAndSend(SMS_EXCHANGE_NAME, VERIFY_CODE_KEY, msg);

    }
}