package com.leyou.item.service;

import com.leyou.common.exception.LyException;
import com.leyou.common.exception.enums.ExceptionEnum;
import com.leyou.common.utils.BeanHelper;
import com.leyou.item.dto.SpecGroupDTO;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    /**
     * 根据分类id查询规格组及其组内的参数
     * @param cid
     * @return
     */
    public List<SpecGroupDTO> findSpecGroupByCid(Long cid) {
        // 根据分类id查询组
        List<SpecGroup> specGroupList = findSpecGroupByCategoryId(cid);
        // 转成DTO
        List<SpecGroupDTO> specGroupDTOList = BeanHelper.copyWithCollection(specGroupList, SpecGroupDTO.class);

        // 查询规格参数的方式三：通过分类id一次性查询出来 【高级】 ：  只有两条
        List<SpecParam> specParams = findSpecParamByGroupId(null, cid, null);
        // 使用jdk1.8的流的特性： 分组  :  返回的是map  ：  key： groupId规格组的id  ， value:  规格参数list集合
        Map<Long, List<SpecParam>> specMap = specParams.stream().collect(Collectors.groupingBy(SpecParam::getGroupId));
        // 迭代组
        for (SpecGroupDTO groupDTO : specGroupDTOList) {
            groupDTO.setParams(specMap.get(groupDTO.getId()));
        }



        // 查询规格参数的方式二：通过分类id一次性查询出来 【中级】 ：  只有两条
        /*List<SpecParam> specParams = findSpecParamByGroupId(null, cid, null);
        // 第一层for循环先迭代组
        for (SpecGroupDTO groupDTO : specGroupDTOList) {
            // 第二层for循环： 规格参数：  通过规格参数的组id和groupDTO的id做对比
            for (SpecParam specParam : specParams) {
                if(specParam.getGroupId().equals(groupDTO.getId())){
                    // 如果进入这里，说明规格参数属于这个规格组
                    if(groupDTO.getParams() == null){
                        groupDTO.setParams(new ArrayList<SpecParam>());
                    }
                    groupDTO.getParams().add(specParam);
                }
            }
        }*/



        // 查询规格参数的方式一： 根据组的id查询规格参数 : 入门级写法： 在for循环操作数据库  ：  一堆sql
        /*for (SpecGroupDTO specGroupDTO : specGroupDTOList) {
            List<SpecParam> specParams = findSpecParamByGroupId(specGroupDTO.getId(), null, null);
            specGroupDTO.setParams(specParams);
        }*/
        //返回
        return specGroupDTOList;
    }
}
