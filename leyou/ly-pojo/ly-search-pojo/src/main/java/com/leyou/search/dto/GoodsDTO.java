package com.leyou.search.dto;

import lombok.Data;

/**
 * 用来返回给前端的
 */
@Data
public class GoodsDTO {
    private Long id; // spuId
    private String spuName;// spu名称
    private String subTitle;// 卖点
    private String skus;// sku信息的json结构
}