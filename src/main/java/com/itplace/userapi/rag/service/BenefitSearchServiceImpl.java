package com.itplace.userapi.rag.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.KnnQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itplace.userapi.recommend.dto.Candidate;
import java.io.IOException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BenefitSearchServiceImpl implements BenefitSearchService {

    private final ElasticsearchClient esClient;
    private final ObjectMapper objectMapper;

    public List<Candidate> queryVector(List<Float> userEmbedding, int CandidateSize) {
        try {
            KnnQuery knnQuery = KnnQuery.of(k -> k
                    .field("embedding")
                    .k(CandidateSize)
                    .numCandidates(100)
                    .queryVector(userEmbedding)
            );

            SearchRequest request = SearchRequest.of(s -> s
                    .index("benefit")
                    .knn(knnQuery)
                    .size(13) //default
            );

            SearchResponse<JsonData> response = esClient.search(request, JsonData.class);
            return response.hits().hits().stream()
                    .map(hit -> {
                        JsonData source = hit.source();
                        JsonNode node = source.to(JsonNode.class);

                        return Candidate.builder()
                                .partnerId(node.get("partnerId").asLong())
                                .benefitId(node.get("benefitId").asLong())
                                .benefitName(node.get("benefitName").asText())
                                .partnerName(node.get("partnerName").asText())
                                .category(node.get("category").asText())
                                .description(node.get("description").asText())
                                .context(node.get("context").asText())
                                .build();
                    })
                    .toList();
        } catch (IOException e) {
            throw new RuntimeException("ES 유사도 검색 실패", e);
        }
    }


}

