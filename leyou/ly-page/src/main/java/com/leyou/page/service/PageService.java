package com.leyou.page.service;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpecGroupDTO;
import com.leyou.item.dto.SpuDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class PageService {
    
    @Autowired
    private ItemClient itemClient;

    /**
     * 根据spu的id查询商品详情页所有的数据
     *
     * 以前的做法： 我们查询到之后，先把数据放入redis中
     *
     * @param spuId
     * @return
     */
    public Map<String, Object> findGoodsData(Long spuId) {
        // 3、 根据spuid查询SpuDTO
        SpuDTO spuDTO = itemClient.findSpuById(spuId);
        // 4、查询分类列表
        List<CategoryDTO> categoryDTOList = itemClient.findCategoryListByIds(spuDTO.getCategoryIds());
        // 5、查询品牌
        BrandDTO brand = itemClient.findBrandById(spuDTO.getBrandId());
        // 6、查询规格组和组内参数
        List<SpecGroupDTO> specGroup = itemClient.findSpecGroupByCid(spuDTO.getCid3());

        // 1、 准备返回的map对象
        Map<String, Object> data = new HashMap<>();
        data.put("categories", categoryDTOList); // 三级分类对象集合
        data.put("brand", brand); // 品牌对象
        data.put("spuName", spuDTO.getName());// spu的名称
        data.put("subTitle", spuDTO.getSubTitle());// spu的副标题
        data.put("detail", spuDTO.getSpuDetail());// SpuDetail对象
        data.put("skus", spuDTO.getSkus());// spu对应的sku的集合
        data.put("specs", specGroup);// 规格参数列表： 先查出spec_group: 然后在组内添加一个 params 的属性
        // 2、返回数据
        return data;
    }

    /**
     * 生成静态页面的方法
     * @param spuId
     */
    @Value("${ly.static.itemDir}")
    private String itemDir;

    @Value("${ly.static.itemTemplate}")
    private String itemTemplate;

    @Autowired
    private SpringTemplateEngine templateEngine; // 模板引擎

    public void createItemHtml(Long spuId){
        // 准备上下文对象
        Context context = new Context();
        context.setVariables(findGoodsData(spuId));

        // 准备静态页输出的路径
        File item = new File(itemDir);
        // 指定静态页的名称
        String itemFileName = spuId + ".html"; // new StringBuilder().append(spuId).append(".html").toString();
        // 生成一个writer: 把流对象放到try中，可以省略关闭流
        try(PrintWriter writer = new PrintWriter(new File(item, itemFileName))) {
            // 生成静态页
            templateEngine.process(itemTemplate, context, writer);
        } catch (Exception e) {
            e.printStackTrace();
            throw new LyException(ExceptionEnum.FILE_WRITER_ERROR);
        }
    }

    /**
     * 删除静态页
     * @param spuId
     */
    public void deleteStaicItemHtml(Long spuId) {
        // 目录
        File file = new File(itemDir);
        // 文件名称
        String fileName = spuId + ".html";
        // 删除文件
        File itemHtml = new File(file, fileName);
        if(itemHtml.exists()){
            itemHtml.delete();
        }
    }
}
