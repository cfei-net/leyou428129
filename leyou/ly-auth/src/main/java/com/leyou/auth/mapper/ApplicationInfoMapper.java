package com.leyou.auth.mapper;

import com.leyou.auth.entity.ApplicationInfo;
import com.leyou.common.mapper.LyBaseMapper;

import java.util.List;

public interface ApplicationInfoMapper extends LyBaseMapper<ApplicationInfo> {

    /**
     * 根据服务id查询他相应的targetId： 也就是服务id对应的权限列表
     * @param serviceId 服务id
     * @return          他能访问那些微服务的id
     */
    List<Long> queryTargetIdList(Long serviceId);
}
