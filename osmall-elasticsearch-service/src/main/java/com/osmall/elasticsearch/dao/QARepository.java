package com.osmall.elasticsearch.dao;
import com.osmall.elasticsearch.entity.QA;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

/**
 * Created by yaoyongzhen on 18/4/29
 */
public interface QARepository extends ElasticsearchRepository<QA,Long> {

}
