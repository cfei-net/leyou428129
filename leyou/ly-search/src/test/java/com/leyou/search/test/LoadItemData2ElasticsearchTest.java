package com.leyou.search.test;

import com.leyou.LySearchApplication;
import com.leyou.common.pojo.PageResult;
import com.leyou.item.client.ItemClient;
import com.leyou.item.dto.SpuDTO;
import com.leyou.search.entity.Goods;
import com.leyou.search.repository.SearchRepository;
import com.leyou.search.service.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = LySearchApplication.class)
public class LoadItemData2ElasticsearchTest {

    @Autowired
    private ItemClient itemClient;

    @Autowired
    private SearchService searchService;

    @Autowired
    private SearchRepository searchRepository;

    @Test
    public void buildGoods() {
        Integer page = 1, rows = 20, totalPage = 1;

        do {
            // 分页查询spu
            PageResult<SpuDTO> pageResult = itemClient.findSpuByPage(null, true, page, rows);
            // 给总页数赋值
            totalPage = pageResult.getTotalPage();
            // 输出日志
            log.info("【把商品数据导入到ES中】当前 第 {} 页  ====>", page );
            // 把数据批量插入es中
            /*
            List<Goods> goodsList = new ArrayList<>();
            for (SpuDTO spuDTO : pageResult.getItems()) {
                Goods goods = searchService.buildGoods(spuDTO);
                //searchRepository.save(goods);
                goodsList.add(goods);
            }
            */
            List<Goods> goodsList = pageResult.getItems().stream().map(searchService::buildGoods).collect(Collectors.toList());
            // 批量保存商品数据到ES中
            searchRepository.saveAll(goodsList);
            // 执行完当前页，执行到下一页
            page++;

        } while (page <= totalPage);
    }
}