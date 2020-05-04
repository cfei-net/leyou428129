package com.leyou.item.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.pojo.PageResult;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.BrandDTO;
import com.leyou.item.entity.Brand;
import com.leyou.item.mapper.BrandMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
@Transactional
public class BrandService {

    @Autowired
    private BrandMapper brandMapper;

    public PageResult<BrandDTO> findBrandByPage(String key, Integer page, Integer rows, String sortBy, Boolean desc) {
        // 1、分页条件设置
        PageHelper.startPage(page, rows);
        // 2、封装查询条件
        Example example = new Example(Brand.class);  // select * from tb_brand
        // 2.1 封装条件对象
        Example.Criteria criteria = example.createCriteria();  // 这个是封装条件部分：  where 后面
        // 2.2 封装查询条件
        if(StringUtils.isNotBlank(key)){
            // id = ?  or   name like ?  or  letter=?
            criteria.orEqualTo("id", key);
            criteria.orLike("name", "%" + key + "%");
            criteria.orEqualTo("letter", key);
        }
        // 2.3 排序
        if(StringUtils.isNotBlank(sortBy)){
            // order by id desc|asc
            example.setOrderByClause(sortBy + " " + (desc ? "desc" : "asc")); // 此方法内部已经写死了 ： order by，所以我们只需要写后面的部分即可，中间有一个空格
        }
        // 3、执行查询
        List<Brand> brandList = brandMapper.selectByExample(example);
        // 4、获取分页数据
        PageInfo<Brand> pageInfo = new PageInfo<>(brandList);
        long total = pageInfo.getTotal(); // 总记录数
        int totalPages = pageInfo.getPages();// 总页数
        List<Brand> list = pageInfo.getList();// 当前页数据
        // 5、封装数据并且返回
        return new PageResult<BrandDTO>(total, totalPages, BeanHelper.copyWithCollection(list, BrandDTO.class));
    }

    public void saveBrand(BrandDTO brandDTO, List<Long> cids) {
        // 先保存品牌表
        Brand brand = BeanHelper.copyProperties(brandDTO, Brand.class);
        int count = brandMapper.insertSelective(brand);// 保存非空的数据
        if(count != 1){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
        // 再保存中间表
        count = brandMapper.insertBrandAndCategory(brand.getId(), cids);
        if(count != cids.size()){
            throw new LyException(ExceptionEnum.INSERT_OPERATION_FAIL);
        }
    }
}
