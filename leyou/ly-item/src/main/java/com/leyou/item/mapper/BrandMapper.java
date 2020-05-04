package com.leyou.item.mapper;

import com.leyou.common.mapper.LyBaseMapper;
import com.leyou.item.entity.Brand;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface BrandMapper extends LyBaseMapper<Brand> {
    /**
     * 插入中间表数据
     * @param id
     * @param cids
     * @return 返回插入的条数
     */
    int insertBrandAndCategory(@Param("bid") Long id,@Param("cids") List<Long> cids);
}
