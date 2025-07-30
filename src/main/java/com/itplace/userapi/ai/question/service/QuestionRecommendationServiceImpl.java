package com.itplace.userapi.ai.question.service;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import com.itplace.userapi.ai.llm.dto.RecommendationResponse;
import com.itplace.userapi.ai.llm.service.OpenAIService;
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
    private final OpenAIService openAIService;

    public RecommendationResponse recommendByQuestion(String question, double lat, double lng) throws Exception {
        // 사용자 질문 임베딩
        List<Float> embedding = embeddingService.embed(question);

        // ES에서 top1 검색
        SearchResponse<Map> response = esClient.search(s -> s
                        .index("questions")
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
            throw new IllegalStateException("관련된 질문을 찾을 수 없습니다.");
        }

        // top1 문서에서 category 추출
        Hit<Map> topHit = hits.get(0);
        double score = topHit.score() != null ? topHit.score() : 0.0;

        // score 임계값 체크
        if (score < 0.7) {
            throw new IllegalStateException("적절한 카테고리를 찾을 수 없습니다 (score: " + score + ")");
        }

        String category = (String) topHit.source().get("category");

        // 제휴처 목록 조회
        List<StoreDetailDto> stores = storeService.findNearbyByKeyword(lat, lng, null, category);

        List<String> partnerNames = stores.stream()
                .map(s -> s.getPartner().getPartnerName())
                .distinct()
                .limit(5)
                .toList();

        // 추천 이유 생성
        String reason = openAIService.generateReasons(question, category, partnerNames);

        // partnerName + imgUrl 조립
        List<RecommendationResponse.PartnerSummary> partners = partnerNames.stream()
                .map(name -> {
                    String imgUrl = stores.stream()
                            .filter(s -> s.getPartner().getPartnerName().equals(name))
                            .findFirst()
                            .map(s -> s.getPartner().getImage())
                            .orElse(null);
                    return RecommendationResponse.PartnerSummary.builder()
                            .partnerName(name)
                            .imgUrl(imgUrl)
                            .build();
                }).toList();

        // 최종 응답 조립
        return RecommendationResponse.builder()
                .reason(reason)
                .partners(partners)
                .build();
    }
}