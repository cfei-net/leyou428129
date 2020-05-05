package com.leyou.item.service;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.mapper.SpecGroupMapper;
import com.leyou.item.mapper.SpecParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

@Service
@Transactional(
        propagation = Propagation.REQUIRED,
        isolation = Isolation.DEFAULT
)
public class SpecService {

    @Autowired
    private SpecGroupMapper specGroupMapper;
    @Autowired
    private SpecParamMapper specParamMapper;


    /**
     * 根据分类id查询规格组
     * @param cid 分类的id
     * @return  规格组列表
     */
    public List<SpecGroup> findSpecGroupByCategoryId(Long cid) {
        SpecGroup record = new SpecGroup();
        record.setCid(cid);
        List<SpecGroup> specGroups = specGroupMapper.select(record);
        if(CollectionUtils.isEmpty(specGroups)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        return specGroups;
    }
}
