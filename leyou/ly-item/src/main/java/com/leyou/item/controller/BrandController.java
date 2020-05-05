package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/brand")
public class BrandController {

    @Autowired
    private BrandService brandService;


    /**
     * 分页查询品牌信息
     * @param key       搜索条件
     * @param page      当前页
     * @param rows      页大小
     * @param sortBy    排序字段
     * @param desc      是否是降序： desc
     * @return          返回通用的分页对象
     */
    @GetMapping("/page")
    public ResponseEntity<PageResult<BrandDTO>> findBrandByPage(
            @RequestParam(value="key",required = false) String key,
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="rows",defaultValue = "5") Integer rows,
            @RequestParam(value="sortBy",required = false) String sortBy,
            @RequestParam(value="desc",required = false) Boolean desc
    ){
        PageResult<BrandDTO> pageResult = brandService.findBrandByPage(key, page, rows, sortBy, desc);
        return ResponseEntity.ok(pageResult);
    }


    /**
     * 保存品牌数据
     * @param brand     品牌的DTO接收参数
     * @param cids      接收分类的id集合
     * @return          没有返回值
     */
    @PostMapping
    public ResponseEntity<Void> saveBrand(BrandDTO brand, @RequestParam("cids") List<Long> cids){
        brandService.saveBrand(brand, cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 根据id查询品牌信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseEntity<BrandDTO> findBrandById(@PathVariable("id") Long id){
        BrandDTO brandDTO = brandService.findBrandById(id);
        return ResponseEntity.ok(brandDTO);
    }



}
