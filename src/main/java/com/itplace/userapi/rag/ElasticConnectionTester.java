package com.itplace.userapi.rag;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.InfoResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ElasticConnectionTester implements ApplicationRunner {

    private final ElasticsearchClient elasticsearchClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        InfoResponse info = elasticsearchClient.info();
        System.out.println("Elasticsearch 연결됨 야호: " + info.version().number());
    }
}

