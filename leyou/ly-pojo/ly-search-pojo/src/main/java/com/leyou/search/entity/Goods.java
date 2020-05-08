package com.leyou.search.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;
import java.util.Set;

/**
 * 一个SPU对应一个Goods
 *      FieldType.Keyword   ：  不分词
 *      FieldType.Text      ：  分词
 */
@Data
@Document(
        indexName = "goods", // es的索引库名称： 相当于mysql的数据库
        type = "docs",  // 类型：相当于mysql的表
        shards = 1,  // 分片，默认5个分片
        replicas = 1 // 副本数
)
public class Goods {
    @Id
    @Field(type = FieldType.Keyword) // 关键字： 不分词
    private Long id; // spuId

    @Field(type = FieldType.Keyword, index = false) // 不分词，不索引【不搜索】
    private String subTitle;// 卖点

    @Field(type = FieldType.Text, analyzer = "ik_max_word") // 分词，分词器
    private String spuName; //spu的名称做高亮用

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String all; // 所有需要被搜索的信息，包含标题，分类，甚至品牌

    private Long brandId;// 品牌id          ： 走默认值
    private Long categoryId;// 商品3级分类id ： 走默认值

    private Map<String, Object> specs;// 可搜索的规格参数，key是参数名，值是参数值

    @Field(type = FieldType.Keyword, index = false) // 不分词也不索引
    private String skus;// sku信息的json结构

    private Long createTime;// spu创建时间【准备这个字段，但我们没有做这个字段的排序】
    private Set<Long> price;// 价格【准备这个字段，但我们没有做这个字段的排序】
}
