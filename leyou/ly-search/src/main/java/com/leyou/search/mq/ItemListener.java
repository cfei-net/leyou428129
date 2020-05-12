package com.leyou.search.mq;

import com.leyou.common.constants.MQConstants;
import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 搜索微服务的消费者：
 *      1、生成索引
 *      2、删除索引
 */
@Slf4j
@Component
public class ItemListener {

    @Autowired
    private SearchService searchService;

    /**
     * 上架：  生成索引
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value= MQConstants.Queue.SEARCH_ITEM_UP,durable = "true"), // 定义队列的名称和是否持久化
                    exchange = @Exchange(
                            value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,
                            type = ExchangeTypes.TOPIC,
                            // springboot会自动创建交换机：如果为true：创建交换机失败之后继续创建其他属性
                            ignoreDeclarationExceptions = "true"
                    ),
                    key = MQConstants.RoutingKey.ITEM_UP_KEY
            )
    )
    public void createIndex(Long spuId){
        log.info("【搜索微服务】生成索引，商品id=======>{}",spuId);
        searchService.createIndex(spuId);
    }


    /**
     * 下架： 删除索引
     * @param spuId
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value= MQConstants.Queue.SEARCH_ITEM_DOWN,durable = "true"), // 定义队列的名称和是否持久化
                    exchange = @Exchange(
                            value = MQConstants.Exchange.ITEM_EXCHANGE_NAME,
                            type = ExchangeTypes.TOPIC,
                            // springboot会自动创建交换机：如果为true：创建交换机失败之后继续创建其他属性
                            ignoreDeclarationExceptions = "true"
                    ),
                    key = MQConstants.RoutingKey.ITEM_DOWN_KEY
            )
    )
    public void deleteIndex(Long spuId){
        log.info("【搜索微服务】删除索引，商品id=======>{}",spuId);
        searchService.deleteIndex(spuId);
    }


}
