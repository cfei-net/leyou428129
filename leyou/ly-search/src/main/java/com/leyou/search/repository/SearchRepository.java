package com.leyou.search.repository;

import com.leyou.search.entity.Goods;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * 我们继承了SpringDataElasticSearch的Repository就具备了索引库的CRUD的功能
 * ElasticsearchRepository : 里面有两个泛型：
 *      1）第一个是我们操作的实体类
 *      2）主键的类型
 */
public interface SearchRepository extends ElasticsearchRepository<Goods, Long> {
}
