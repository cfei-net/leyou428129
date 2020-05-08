package com.leyou.search.service;

import com.leyou.search.repository.SearchRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SearchService {

    @Autowired
    private SearchRepository goodsRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;


}
