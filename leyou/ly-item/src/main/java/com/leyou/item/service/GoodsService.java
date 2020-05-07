package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.dto.SpuDTO;
import com.leyou.item.entity.Sku;
import com.leyou.item.entity.Spu;
import com.leyou.item.entity.SpuDetail;
import com.leyou.item.mapper.SkuMapper;
import com.leyou.item.mapper.SpuDetailMapper;
import com.leyou.item.mapper.SpuMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
public class GoodsService {

    @Autowired
    private SpuMapper spuMapper;
    @Autowired
    private SpuDetailMapper spuDetailMapper;
    @Autowired
    private SkuMapper skuMapper;

    @Autowired
    private BrandService brandService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 根据条件分页查询商品：spu
     * @param key           搜索条件
     * @param saleable      是否上下架
     * @param page          当前页
     * @param rows          页大小
     * @return              当前页的分页数据
     */
    public PageResult<SpuDTO> findSpuByPage(String key, Boolean saleable, Integer page, Integer rows) {
        // 1、分页
        PageHelper.startPage(page, rows);
        // 2、查询条件
        Example example = new Example(Spu.class);
        Example.Criteria criteria = example.createCriteria();
        // 2.1 封装查询条件
        if(StringUtils.isNotBlank(key)){
            criteria.andLike("name", "%" + key + "%");
        }
        // 2.2 封装是否上下架
        if(saleable != null){
            criteria.andEqualTo("saleable", saleable);
        }
        // 3、执行查询
        List<Spu> spuList = spuMapper.selectByExample(example);
        if(CollectionUtils.isEmpty(spuList)){
            throw new LyException(ExceptionEnum.GOODS_NOT_FOUND);
        }
        // 4、获取分页数据
        PageInfo<Spu> pageInfo = new PageInfo<>(spuList);
        // 4.1 总页数
        int totalPages = pageInfo.getPages();
        // 4.2 总记录数
        long total = pageInfo.getTotal();
        // 4.3 当前页数据
        List<Spu> list = pageInfo.getList();
        // 4.4 转换DTO： 此时，分类和品牌只有id，还没有他们的名称
        List<SpuDTO> spuDTOS = BeanHelper.copyWithCollection(list, SpuDTO.class);
        // 4.5 把分类和品牌的名称处理
        handlerCategoryNameAndBrandName(spuDTOS);
        // 5、封装分页对象并返回
        return new PageResult<SpuDTO>(
                total,
                totalPages,
                spuDTOS
        );
    }

    /**
     * 把分类的名称和品牌的名称补全
     * @param spuDTOS       spu的列表数据
     *
     */
    private void handlerCategoryNameAndBrandName(List<SpuDTO> spuDTOS) {
        for (SpuDTO spuDTO : spuDTOS) {
            // 品牌名称
            Long brandId = spuDTO.getBrandId();
            BrandDTO brand = brandService.findBrandById(brandId);
            spuDTO.setBrandName(brand.getName());
            // 分类名称
            List<Long> categoryIds = spuDTO.getCategoryIds();
            List<CategoryDTO> categoryList = categoryService.findCategoryListByIds(categoryIds);
            String categoryNames = categoryList.stream()
                    .map(CategoryDTO::getName)
                    .collect(Collectors.joining("/"));
            spuDTO.setCategoryName(categoryNames);

        }
    }

    /**
     * 保存商品数据
     * @param spuDTO
     */
    public void saveGoods(SpuDTO spuDTO) {
        // 1、保存 Spu
        // 1.1 先把DTO对象转成PO对象
        Spu spu = BeanHelper.copyProperties(spuDTO, Spu.class);
        spu.setSaleable(false);// 设置为下架
        spu.setCreateTime(new Date());// 创建时间
        // 1.2 保存Spu
        int count = spuMapper.insertSelective(spu);//保存非空的数据
        if(count != 1 ){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 1.3 当我们插入了spu，spu对象中有一个@KeySql注解，这个注解可以帮我们自动回填id
        Long spuId = spu.getId();


        // 2、保存 SpuDetail
        SpuDetail spuDetail = spuDTO.getSpuDetail();
        spuDetail.setSpuId(spuId); // spu的id
        count = spuDetailMapper.insertSelective(spuDetail);
        if(count != 1 ){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 3、保存Sku列表
        List<Sku> skus = spuDTO.getSkus();
        // 3.1 设置spu的id
        skus.forEach(s -> {
            s.setSpuId(spuId); // spu的ID
            s.setCreateTime(new Date());
        });
        // 3.2 保存sku列表
        count = skuMapper.insertList(skus); // 批量保存
        if(count != skus.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }

    /**
     * 更新商品的上下架功能
     * @param spuId     商品的id
     * @param saleable  是否上下架：  true:上架； false： 下架
     * @return          没有返回值
     */
    public void updateGoodsSaleable(Long spuId, Boolean saleable) {
        /**
         * updateByPrimaryKeySelective: 根据主键更新对象中非空的字段
         * update tb_spu set saleable = ? where id = ?
         */
        Spu record = new Spu();
        record.setId(spuId);
        record.setSaleable(saleable);
        int count = spuMapper.updateByPrimaryKeySelective(record);
        if (count != 1){
            throw new LyException(ExceptionEnum.UPDATE_OPERATION_FAIL);
        }
    }
}
