package com.leyou.common.auth.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 微服务的载荷信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppInfo {
    private Long id;  // 微服务id
    private String serviceName;// 微服务名称
    private List<Long> targetList;// 微服务的权限列表，他具有访问那些微服务
}
