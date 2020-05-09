package com.leyou.item.client;

import com.leyou.common.pojo.PageResult;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.SpecParam;
import com.leyou.item.entity.SpuDetail;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * feign接口是给其他微服务调用的，一般不关注状态码，所以我们的返回值不用写
 *  ResponseEntity: 包含了状态码和响应的值
 *  微服务之间的调用我们只关心值就够了
 */
@FeignClient("item-service")
public interface ItemClient {
    /**
     * 根据条件分页查询商品：spu
     * @param key           搜索条件
     * @param saleable      是否上下架
     * @param page          当前页
     * @param rows          页大小
     * @return              当前页的分页数据
     */
    @GetMapping("/spu/page")
    public PageResult<SpuDTO> findSpuByPage(
            @RequestParam(value="key",required = false) String key,
            @RequestParam(value="saleable",required = false) Boolean saleable,
            @RequestParam(value="page",defaultValue = "1") Integer page,
            @RequestParam(value="rows",defaultValue = "5") Integer rows
    );


    /**
     * 根据spu的id查询sku列表
     * @param spuId
     * @return
     */
    @GetMapping("/sku/of/spu")
    public List<Sku> findSkuBySpuID(@RequestParam("id") Long spuId);


    /**
     * 根据spu的id查询spu详情
     * @param spuId
     * @return
     */
    @GetMapping("/spu/detail")
    public SpuDetail findSpuDetailBySpuID(@RequestParam("id") Long spuId);

    /**
     * 查询规格参数：多个条件
     *
     * @param gid       规格组的id
     * @param cid       分类的id
     * @param searching 是否是搜索过滤参数
     * @return
     */
    @GetMapping("/spec/params")
    public List<SpecParam> findSpecParamByGroupId(
            @RequestParam(value = "gid", required = false) Long gid,
            @RequestParam(value = "cid", required = false) Long cid,
            @RequestParam(value = "searching", required = false) Boolean searching
    );

    /**
     * 根据分类的id集合查询分类列表
     * @param ids
     * @return
     */
    @GetMapping("/category/list")
    public List<CategoryDTO> findCategoryListByIds(@RequestParam("ids") List<Long> ids);


    /**
     * 讲道理我们应该写一个批量查询品牌接口：今天为了简便，使用这个来代替
     */
    @GetMapping("/brand/{id}")
    public BrandDTO findBrandById(@PathVariable("id") Long id);

}
