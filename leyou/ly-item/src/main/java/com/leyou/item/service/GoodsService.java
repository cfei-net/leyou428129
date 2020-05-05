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
import com.leyou.item.entity.Spu;
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

}
