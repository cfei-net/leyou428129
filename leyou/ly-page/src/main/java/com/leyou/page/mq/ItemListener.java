package com.leyou.page.mq;

import com.leyou.common.constants.MQConstants;
import com.leyou.page.service.PageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ItemListener {

    @Autowired
    private PageService pageService;

    /**
     * 上架： 生成静态页
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value= MQConstants.Queue.PAGE_ITEM_UP,durable = "true"), // 定义队列的名称和是否持久化
                    exchange = @Exchange(
                            value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,
                            type = ExchangeTypes.TOPIC,
                            // springboot会自动创建交换机：如果为true：创建交换机失败之后继续创建其他属性
                            ignoreDeclarationExceptions = "true"
                    ),
                    key = MQConstants.RoutingKey.ITEM_UP_KEY
            )
    )
    public void createStaicItemHtml(Long spuId){
        log.info("【静态页服务】，生成静态页：商品id=======>{}",spuId);
        pageService.createItemHtml(spuId);
    }


    /**
     * 上架： 生成静态页
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value= MQConstants.Queue.PAGE_ITEM_DOWN,durable = "true"), // 定义队列的名称和是否持久化
                    exchange = @Exchange(
                            value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,
                            type = ExchangeTypes.TOPIC,
                            // springboot会自动创建交换机：如果为true：创建交换机失败之后继续创建其他属性
                            ignoreDeclarationExceptions = "true"
                    ),
                    key = MQConstants.RoutingKey.ITEM_DOWN_KEY
            )
    )
    public void deleteStaicItemHtml(Long spuId){
        log.info("【静态页服务】，删除静态页：商品id=======>{}",spuId);
        pageService.deleteStaicItemHtml(spuId);
    }
}
