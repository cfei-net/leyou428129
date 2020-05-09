package com.leyou.search.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.search.dto.GoodsDTO;
import com.leyou.search.dto.SearchRequest;
import com.leyou.search.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 根据条件分页搜索商品数据
     * @param request 【SearchRequest】  封装搜索条件： 搜索条件和当前页
     * @return  分页对象
     */
    @PostMapping("/page")
    public ResponseEntity<PageResult<GoodsDTO>> findGoodsByPage(@RequestBody SearchRequest request){
        PageResult<GoodsDTO> pageResult = searchService.findGoodsByPage(request);
        return ResponseEntity.ok(pageResult);
    }
}
