package com.leyou.auth.entity;

import lombok.Data;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

/**
 * 微服务的信息表
 */
@Data
@Table(name = "tb_application")
public class ApplicationInfo {
    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;
    private String serviceName;//服务名称
    private String secret;//服务密钥
    private String info;//服务信息
    private Date createTime;//创建时间
    private Date updateTime;//更新时间
}