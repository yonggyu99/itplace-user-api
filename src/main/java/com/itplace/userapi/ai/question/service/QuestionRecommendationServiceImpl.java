package com.itplace.userapi.ai.question.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.itplace.userapi.ai.rag.service.EmbeddingService;
import com.itplace.userapi.map.dto.StoreDetailDto;
import com.itplace.userapi.map.service.StoreService;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QuestionRecommendationServiceImpl implements QuestionRecommendationService {
    private final EmbeddingService embeddingService;
    private final ElasticsearchClient esClient;
    private final StoreService storeService;

    public List<StoreDetailDto> recommendByQuestion(String question, double lat, double lng) throws Exception {
        // 1. 사용자 질문 임베딩
        List<Float> embedding = embeddingService.embed(question);

        // 2. ES에서 top1 검색
        SearchResponse<Map> response = esClient.search(s -> s
                        .index("question_embeddings")
                        .knn(knn -> knn
                                .field("embedding")
                                .k(1)
                                .numCandidates(10)
                                .queryVector(embedding)
                        ),
                Map.class
        );

        List<Hit<Map>> hits = response.hits().hits();
        if (hits.isEmpty()) {
            throw new IllegalStateException("관련된 질문 카테고리를 찾을 수 없습니다.");
        }

        // 3. top1 문서에서 category 추출
        Map<String, Object> topHit = hits.get(0).source();
        String category = (String) topHit.get("category");

        // 4. storeService를 통해 사용자 위치 기준 제휴처 조회
        return storeService.findNearbyByKeyword(lat, lng, category, null);
    }
}
