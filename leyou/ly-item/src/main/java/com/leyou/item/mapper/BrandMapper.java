package com.leyou.item.mapper;

import com.leyou.common.mapper.LyBaseMapper;
import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface BrandMapper extends LyBaseMapper<Brand> {
    /**
     * 插入中间表数据
     * @param id
     * @param cids
     * @return 返回插入的条数
     */
    int insertBrandAndCategory(@Param("bid") Long id,@Param("cids") List<Long> cids);

    /**
     * 根据分类id查询品牌列表
     * @param cid   分类id
     * @return
     */
    @Select("select t1.* from tb_brand t1,tb_category_brand t2 where t1.id = t2.brand_id and category_id = #{cid}")
    List<Brand> findBrandListByCategoryId(Long cid);
}
