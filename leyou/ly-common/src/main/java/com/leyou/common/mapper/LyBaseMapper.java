package com.leyou.common.mapper;

import tk.mybatis.mapper.additional.idlist.IdListMapper;

import tk.mybatis.mapper.annotation.RegisterMapper;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
//import tk.mybatis.mapper.additional.insert.InsertListMapper; // 不支持主键策略，插入前需要设置好主键的值
import tk.mybatis.mapper.common.special.InsertListMapper; // 批量插入，另外该接口限制实体包含`id`属性并且必须为自增列

@RegisterMapper
public interface LyBaseMapper<T> extends Mapper<T>, IdListMapper<T, Long>, IdsMapper<T>, InsertListMapper<T> {

    // 这里如果没有自己扩展的方法的话，可以省略： @RegisterMapper
}
