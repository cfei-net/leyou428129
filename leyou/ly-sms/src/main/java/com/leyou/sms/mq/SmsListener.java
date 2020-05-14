package com.leyou.sms.mq;

import com.leyou.common.constants.MQConstants;
import com.leyou.sms.utils.SmsUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SmsListener {

    @Autowired
    private SmsUtils smsUtils;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value= MQConstants.Queue.SMS_VERIFY_CODE_QUEUE,durable = "true"), // 定义队列的名称和是否持久化
                    exchange = @Exchange(
                            value = MQConstants.Exchange.SMS_EXCHANGE_NAME,
                            type = ExchangeTypes.TOPIC,
                            // springboot会自动创建交换机：如果为true：创建交换机失败之后继续创建其他属性
                            ignoreDeclarationExceptions = "true"
                    ),
                    key = MQConstants.RoutingKey.VERIFY_CODE_KEY
            )
    )
    public void sendSms(Map<String, String> msg){
        try {
            // 1、获取手机号码
            String phone = msg.get("phone");
            // 2、获取随机验证码
            String code = msg.get("code");
            // 3、发送短信
            smsUtils.sendSms(phone, code);
        } catch (Exception e) {
            e.printStackTrace();
            log.info("【短信微服务】发送短信异常： {}", e.getMessage());
        }

    }
}
