package com.itplace.userapi.recommend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itplace.userapi.benefit.entity.enums.Grade;
import com.itplace.userapi.partner.entity.Partner;
import com.itplace.userapi.partner.repository.PartnerRepository;
import com.itplace.userapi.rag.service.BenefitSearchService;
import com.itplace.userapi.rag.service.EmbeddingService;
import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.ChatCompletionResponse;
import com.itplace.userapi.recommend.dto.Recommendations;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class OpenAIServiceImpl implements OpenAIService {
    private final ObjectMapper mapper;
    private final RestTemplate restTemplate = new RestTemplate();
    private final EmbeddingService embeddingService;
    private final BenefitSearchService benefitSearchService;
    private final PartnerRepository partnerRepository;

    @Value("${spring.ai.openai.api.key}")
    private String apiKey;

    @Value("${spring.ai.openai.api.url}")
    private String baseUrl;

    @Value("${spring.ai.openai.model.chat}")
    private String model;


    @Override
    public List<Candidate> vectorSearch(UserFeature uf, int CandidateSize) {
        List<Float> userEmbedding = embeddingService.embed(uf.getEmbeddingContext());
        Grade grade = uf.getGrade();
        return benefitSearchService.queryVector(grade, userEmbedding, CandidateSize);
    }


    @Override
    public List<Recommendations> rerankAndExplain(UserFeature uf, List<Candidate> cands, int topK) {
        String url = baseUrl + "/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        System.out.println("후보 사이즈: " + cands.size());
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < cands.size(); i++) {
            Candidate c = cands.get(i);
            String description = c.getDescription() != null ? c.getDescription().replaceAll("[\\r\\n]+", " ") : "설명 없음";
            String context = c.getContext() != null ? c.getContext().replaceAll("[\\r\\n]+", " ") : "추가 정보 없음";

            items.append(String.format(
                    "%d. [%s] %s - %s / 혜택: %s\n",
                    i + 1,
                    c.getCategory(),
                    c.getPartnerName(),
                    description,
                    context
            ));
        }

        String prompt = String.format("""
                【사용자 성향 요약】
                %s
                
                【후보 혜택들】
                %s
                
                사용자 성향과 혜택 설명을 바탕으로 가장 적절한 혜택 %d개 이상 골라주세요.
                추천 이유에는 반드시 혜택에 대한 내용을 반영해야합니다.
                제휴사는 중복되지 않도록 해주세요.
                
                "Don't include markdown formatting. Just return valid JSON only."
                {
                  "recommendations": [
                    {
                      "rank": 1,
                      "partnerName": "뚜레쥬르",
                      "reason": "푸드 카테고리에 관심이 많으시고, 뚜레쥬르 혜택이 실속 있어서 추천드리는 걸요!"
                    },
                    ...
                  ]
                }
                """, uf.getEmbeddingContext(), items, topK);

        List<Map<String, String>> messages = List.of(
                Map.of(
                        "role", "system",
                        "content", """
                                당신은 귀엽고 상냥한 우주 토끼 캐릭터 '잇콩'이에요!
                                사용자의 관심사와 혜택 정보를 바탕으로,
                                밝고 따뜻한 말투로 추천을 도와주는 안내 역할을 해요.
                                말투는 '~인 걸요!', '~했다구요!' 같은 어미를 자주 사용하세요.
                                """
                ),
                Map.of("role", "user", "content", prompt)
        );

        Map<String, Object> body = Map.of(
                "model", model,
                "messages", messages
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        long start = System.nanoTime();
        ResponseEntity<ChatCompletionResponse> response = restTemplate
                .exchange(url, HttpMethod.POST, request, ChatCompletionResponse.class);
        long end = System.nanoTime();
        System.out.println("LLM 응답 생성 시간 (ms): " + (end - start) / 1_000_000);
        ChatCompletionResponse cr = response.getBody();
        if (cr != null && !cr.getChoices().isEmpty()) {
            String jsonString = cr.getChoices().get(0).getMessage().getContent();
            try {
                JsonNode root = mapper.readTree(jsonString);
                JsonNode recList = root.get("recommendations");
                List<Recommendations> recommendations = mapper.readerForListOf(Recommendations.class)
                        .readValue(recList);

                for (Recommendations rec : recommendations) {
                    String imgUrl = partnerRepository.findByPartnerName(rec.getPartnerName())
                            .map(Partner::getImage)
                            .orElse("<UNKNOWN>");

                    rec.setImgUrl(imgUrl);

                }

                return recommendations;
            } catch (Exception e) {
                throw new RuntimeException("LLM 추천 결과 파싱 실패", e);
            }
        }
        return List.of();

    }
}

