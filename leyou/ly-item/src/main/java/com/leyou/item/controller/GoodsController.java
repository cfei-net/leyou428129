package com.leyou.item.controller;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    /**
     * 根据条件分页查询商品：spu
     * @param key           搜索条件
     * @param saleable      是否上下架
     * @param page          当前页
     * @param rows          页大小
     * @return              当前页的分页数据
     */
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuDTO>> findSpuByPage(
            @RequestParam(value="key",required = false) String key,
            @RequestParam(value="saleable",required = false) Boolean saleable,
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="rows",defaultValue = "5") Integer rows
    ){
        PageResult<SpuDTO> pageResult = goodsService.findSpuByPage(key, saleable, page, rows);
        return ResponseEntity.ok(pageResult);
    }

    /**
     * 保存商品
     * @param spuDTO
     * @return
     */
    @PostMapping("/goods")
    public ResponseEntity<Void> saveGoods(@RequestBody SpuDTO spuDTO){
        goodsService.saveGoods(spuDTO);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /**
     * 更新商品的上下架功能
     * @param spuId     商品的id
     * @param saleable  是否上下架：  true:上架； false： 下架
     * @return          没有返回值
     */
    @PutMapping("/spu/saleable")
    public ResponseEntity<Void> updateGoodsSaleable(
            @RequestParam("id") Long spuId,
            @RequestParam("saleable") Boolean saleable
    ){
        goodsService.updateGoodsSaleable(spuId, saleable);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    /**
     * 根据spu的id查询sku列表
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    public ResponseEntity<List<Sku>> findSkuBySpuID(@RequestParam("id") Long spuId){
        List<Sku> skuList = goodsService.findSkuBySpuID(spuId);
        return ResponseEntity.ok(skuList);
    }

    /**
     * 根据spu的id查询spu详情
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    public ResponseEntity<SpuDetail> findSpuDetailBySpuID(@RequestParam("id") Long spuId){
        SpuDetail spuDetail = goodsService.findSpuDetailBySpuID(spuId);
        return ResponseEntity.ok(spuDetail);
    }

    /**
     * 根据id去查询spu信息
     * @param spuId
     * @return
     */
    @GetMapping("/spu/{id}")
    public ResponseEntity<SpuDTO> findSpuById(@PathVariable("id") Long spuId){
        SpuDTO spuDTO = goodsService.findSpuById(spuId);
        return ResponseEntity.ok(spuDTO);
    }

    /**
     * 根据sku的id集合查询sku的列表
     * @param ids   sku的id集合，以逗号分隔
     * @return      sku列表
     */
    @GetMapping("/sku/list")
    public ResponseEntity<List<Sku>> findSkuListByIds(@RequestParam("ids") List<Long> ids){
        List<Sku> skuList = goodsService.findSkuListByIds(ids);
        return ResponseEntity.ok(skuList);
    }

}
