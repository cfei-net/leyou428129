package com.leyou.item.service;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.item.entity.SpecGroup;
import com.leyou.item.entity.SpecParam;
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

    /**
     * 查询规格参数：多个条件
     * @param gid           规格组的id
     * @param cid           分类的id
     * @param searching     是否是搜索过滤参数
     * @return
     */
    public List<SpecParam> findSpecParamByGroupId(Long gid, Long cid, Boolean searching) {
        // 查询条件： 规格组id和分类的id，必须二选一
        if(gid == null && cid == null){
            throw new LyException(ExceptionEnum.INVALID_PARAM_ERROR);
        }
        // 查询条件封装
        SpecParam record = new SpecParam();
        record.setGroupId(gid);
        record.setCid(cid);
        record.setSearching(searching);
        List<SpecParam> specParamList = specParamMapper.select(record);
        // 判空
        if(CollectionUtils.isEmpty(specParamList)){
            throw new LyException(ExceptionEnum.SPEC_NOT_FOUND);
        }
        // 返回
        return specParamList;
    }
}
