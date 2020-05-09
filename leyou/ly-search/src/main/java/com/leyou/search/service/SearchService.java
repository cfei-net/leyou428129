package com.leyou.search.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.common.utils.JsonUtils;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.entity.SpuDetail;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.SearchRepository;
import com.leyou.search.utils.HighlightUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.FetchSourceFilter;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private SearchRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Autowired
    private ItemClient itemClient;

    /**
     * 这个方法就是构建一个Goods
     * @param spuDTO    商品对象的来源：spuDTO
     * @return
     */
    public Goods buildGoods(SpuDTO spuDTO){
        // 提取spuId
        Long spuId = spuDTO.getId();

        // 5、根据spuId查询sku集合：封装all、price、skus字段
        List<Sku> skuList = itemClient.findSkuBySpuID(spuId);
        // 5.1 由于用来展示的sku只需要四个属性，所以为了节省空间，我们把列表转成map
        List<Map<String, Object>> skuMapList = new ArrayList<Map<String, Object>>();
        skuList.forEach(sku -> {
            Map<String, Object> s = new HashMap<>();
            s.put("id", sku.getId());
            s.put("title", sku.getTitle());
            s.put("price", sku.getPrice());
            // http://image.leyou.com/202005/sES5Zabwp4Rb8ZGd6HTT.jpg,http://image.leyou.com/202005/xTkTjnChJ84ReinFMwGT.jpg
            s.put("image", StringUtils.substringBefore(sku.getImages(), ","));
            skuMapList.add(s);
        });
        // 5.2 把集合转成字符串
        String skuListStr = JsonUtils.toString(skuMapList);
        // 5.3 流的方式去收集价格
        Set<Long> priceSet = skuList.stream().map(Sku::getPrice).collect(Collectors.toSet());
        // 5.4 拼装all字段： 这个字段用来搜索的  :  品牌名称  +  分类名称  +  商品名称
        String all = spuDTO.getBrandName()
                    + spuDTO.getCategoryName()
                    + spuDTO.getName()
                    + skuList.stream().map(Sku::getTitle).collect(Collectors.joining());

        // 6、根据分类id查询搜索的规格参数： specs
        List<SpecParam> specParams = itemClient.findSpecParamByGroupId(null, spuDTO.getCid3(), true);

        // 7、根据spuId查询SpuDetail
        SpuDetail spuDetail = itemClient.findSpuDetailBySpuID(spuId);
        // 7.1 获取通用规格
        String genericSpecStr = spuDetail.getGenericSpec();// 获取通用的规格参数
        // 7.1.1 把通用规格参数字符串转成对象
        Map<Long, Object> genericSpecMap = JsonUtils.toMap(genericSpecStr, Long.class, Object.class);
        // 7.2 获取特有规格
        String specialSpecStr = spuDetail.getSpecialSpec();
        // 7.2.1 把特有规格转成map的对象
        Map<Long, List<Object>> specialSpecMap =
                JsonUtils.nativeRead(specialSpecStr, new TypeReference<Map<Long, List<Object>>>() {});

        // 我们要key和value放入我们的规格参数map中
        Map<String, Object> specs = new HashMap<>();
        for (SpecParam specParam : specParams) {
            String key = specParam.getName();
            Object value = null;
            if(specParam.getGeneric()){
                // 如果是通用规格参数： 去 genericSpecMap 取值
                value = genericSpecMap.get(specParam.getId());
            } else {
                // 如果是特有规格参数： 去 specialSpecMap 取值
                value =  specialSpecMap.get(specParam.getId());
            }
            // 如果是数字类型的数据，我们提前转成区间字符串值
            if(specParam.getNumeric()){
                value = chooseSegment(value, specParam);
            }
            // 存入过滤规格参数map中
            specs.put(key, value);
        }

        // 1、构建一个Goods对象
        Goods goods = new Goods();
        // 3、把spuDTO有的数据，先保存到Goods中
        goods.setBrandId(spuDTO.getBrandId());
        goods.setCategoryId(spuDTO.getCid3());
        goods.setCreateTime(spuDTO.getCreateTime().getTime());// 获取毫秒数
        goods.setId(spuDTO.getId());
        goods.setSpuName(spuDTO.getName());// 用来高亮展示的内容
        goods.setSubTitle(spuDTO.getSubTitle());
        // 4、没有的数据，列出来
        goods.setSkus(skuListStr);    // 用来在页面展示的sku列表
        goods.setPrice(priceSet);   // 用来排序： 包含所有sku的价格，去重
        goods.setAll(all);     // 搜索字段： 包含用户要搜索的所有信息
        goods.setSpecs(specs);   // 规格参数： 用来搜索过滤
        // 2、返回
        return goods;
    }

    private String chooseSegment(Object value, SpecParam p) {
        if (value == null || StringUtils.isBlank(value.toString())) {
            return "其它";
        }
        double val = parseDouble(value.toString());
        String result = "其它";
        // 保存数值段
        for (String segment : p.getSegments().split(",")) {
            String[] segs = segment.split("-");
            // 获取数值范围
            double begin = parseDouble(segs[0]);
            double end = Double.MAX_VALUE;
            if (segs.length == 2) {
                end = parseDouble(segs[1]);
            }
            // 判断是否在范围内
            if (val >= begin && val < end) {
                if (segs.length == 1) {
                    result = segs[0] + p.getUnit() + "以上";
                } else if (begin == 0) {
                    result = segs[1] + p.getUnit() + "以下";
                } else {
                    result = segment + p.getUnit();
                }
                break;
            }
        }
        return result;
    }

    private double parseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 根据条件分页搜索商品数据
     * @param request 【SearchRequest】  封装搜索条件： 搜索条件和当前页
     * @return  分页对象
     */
    public PageResult<GoodsDTO> findGoodsByPage(SearchRequest request) {
        // 1、使用ES的原生搜索： 封装查询条件
        NativeSearchQueryBuilder nativeSearchQueryBuilder = new NativeSearchQueryBuilder();
        // 1.1 设置我们要返回的结果包含哪些对象
        nativeSearchQueryBuilder.withSourceFilter(new FetchSourceFilter(new String[]{"id","spuName","subTitle","skus"},null));
        // 1.2 设置分页查询条件:  spring的分页对象，从0开始，意味着我们的当前页要减一； 0第一页；1第二页 ......
        nativeSearchQueryBuilder.withPageable(PageRequest.of(request.getPage() -1, request.getSize()));
        // 1.3 设置查询条件
        nativeSearchQueryBuilder.withQuery(handlerQueryParam(request));
        // 1.4 设置高亮搜索
        HighlightUtils.highlightField(nativeSearchQueryBuilder, "spuName");

        // 2、执行搜索
        AggregatedPage<Goods> goodsAgg = elasticsearchTemplate.queryForPage(
                nativeSearchQueryBuilder.build(), // 构建搜索条件
                Goods.class, // 返回的实体类
                HighlightUtils.highlightBody(Goods.class, "spuName") // 返回高亮的body
        );

        // 3、获取搜索的结果数据
        // 3.1 总记录数
        long totalElements = goodsAgg.getTotalElements();
        // 3.2 总页数
        int totalPages = goodsAgg.getTotalPages();
        // 3.3 当前页数据
        List<Goods> goodsList = goodsAgg.getContent();

        // 4、封装结果返回: 把实体类转成DTO返回
        return new PageResult<GoodsDTO>(
                totalElements,
                totalPages,
                BeanHelper.copyWithCollection(goodsList, GoodsDTO.class)
        );
    }

    private QueryBuilder handlerQueryParam(SearchRequest request) {
        return QueryBuilders
                .multiMatchQuery(request.getKey(), "spuName","all")// 参数一：搜索条件；  后面的可变参： 搜索的域
                .operator(Operator.AND); // 把分词后的条件用 and链接
    }
}
