package com.leyou.item.service;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.CategoryDTO;
import com.leyou.item.entity.Category;
import com.leyou.item.mapper.CategoryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional
public class CategoryService {

    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 根据父id查询分类列表
     */
    public List<CategoryDTO> findCategoryByPid(Long pid) {
        // 查询
        Category record = new Category();
        record.setParentId(pid);
        List<Category> categoryList = categoryMapper.select(record);
        // 判断
        if(CollectionUtils.isEmpty(categoryList)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        // 把对象转成dto
        return BeanHelper.copyWithCollection(categoryList, CategoryDTO.class);
    }

    /**
     * 根据分类的id集合查询分类列表信息
     * @param ids
     * @return
     */
    public List<CategoryDTO> findCategoryListByIds(List<Long> ids) {
        List<Category> categories = categoryMapper.selectByIdList(ids);
        // 判断
        if(CollectionUtils.isEmpty(categories)){
            throw new LyException(ExceptionEnum.CATEGORY_NOT_FOUND);
        }
        // 把对象转成dto
        return BeanHelper.copyWithCollection(categories, CategoryDTO.class);
    }
}
