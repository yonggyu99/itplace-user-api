package com.itplace.userapi.rag.service;

public interface ElasticService {
    void createIndexIfNotExists(String indexName);

    boolean existsById(String indexName, String id);
}
