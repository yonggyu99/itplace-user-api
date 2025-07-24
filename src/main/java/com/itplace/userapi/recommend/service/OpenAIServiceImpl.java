package com.itplace.userapi.recommend.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.itplace.userapi.rag.service.BenefitSearchService;
import com.itplace.userapi.rag.service.EmbeddingService;
import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.ChatCompletionResponse;
import com.itplace.userapi.recommend.dto.Recommendation;
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

    @Value("${spring.ai.openai.api.key}")
    private String apiKey;

    @Value("${spring.ai.openai.api.url}")
    private String baseUrl;

    @Value("${spring.ai.openai.model.chat}")
    private String model;


    @Override
    public List<Candidate> vectorSearch(UserFeature uf, int CandidateSize) {
        List<Float> userEmbedding = embeddingService.embed(uf.getEmbeddingContext());
        return benefitSearchService.queryVector(userEmbedding, CandidateSize);
    }


    @Override
    public List<Recommendation> rerankAndExplain(UserFeature uf, List<Candidate> cands, int topK) {
        String url = baseUrl + "/v1/chat/completions";

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        StringBuilder items = new StringBuilder();
        for (int i = 0; i < cands.size(); i++) {
            Candidate c = cands.get(i);
            items.append(String.format(
                    """
                            %d. 혜택명: %s
                               제휴사: %s
                               카테고리: %s
                               설명: %s
                               등급별 혜택 요약 %s
                               이미지 : %s
                            
                            """,
                    i + 1,
                    c.getBenefitName(),
                    c.getPartnerName(),
                    c.getCategory(),
                    c.getDescription() != null ? c.getDescription() : "설명 없음",
                    c.getContext() != null ? c.getContext() : "추가 정보 없음",
                    c.getImgUrl() != null ? c.getImgUrl() : "<UNK> <UNK> <UNK>"
            ));
        }
        String prompt = String.format("""
                【사용자 성향 요약】
                %s
                
                【후보 혜택들】
                %s
                
                당신은 사용자의 등급과 성향을 분석해 가장 적절한 혜택 %d개를 추천해야 해요.
                
                각 혜택의 '설명'을 잘 반영하고 사용자의 '카테고리', '제휴사'를 잘 참고해서
                왜 이 사용자가 해당 후보 혜택을 좋아할지를 추천 이유로 작성해주세요.
                추천 이유에 사용자 등급에 맞는 혜택 내용을 같이 언급하면 좋겠습니다.
                [강조!!] 반드시 제공된 후보 혜택들 중에서만 추천을 진행해야합니다.
                
                [강조!!] 반드시 아래와 같은 JSON 형식으로 응답해주세요:
                "Don't include markdown formatting. Just return valid JSON only."
                {
                  "recommendations": [
                    {
                      "rank": 1,
                      "partnerName": "뚜레쥬르",
                      "reason": "빵이 너무 맛있어서 자주 가는 곳이니까 추천드리는 걸요!"
                      "imgUrl": "https://itplacepartners.s3.ap-northeast-2.amazonaws.com/img/touslesjours.png"
                    },
                    {
                      "rank": 2,
                      "partnerName": "배스킨라빈스",
                      "reason": "달콤한 아이스크림이 요즘처럼 더운 날에 딱이니까요!"
                      "imgUrl": "https://itplacepartners.s3.ap-northeast-2.amazonaws.com/img/baskinrobbins.png"
                    }
                    ...
                  ]
                }
                
                포맷은 꼭 지켜주세요. 문자열이나 문장이 아닌 JSON 객체로만 응답해주세요!
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

        ResponseEntity<ChatCompletionResponse> response = restTemplate
                .exchange(url, HttpMethod.POST, request, ChatCompletionResponse.class);

        ChatCompletionResponse cr = response.getBody();
        if (cr != null && !cr.getChoices().isEmpty()) {
            String jsonString = cr.getChoices().get(0).getMessage().getContent();
            try {
                JsonNode root = mapper.readTree(jsonString);
                JsonNode recList = root.get("recommendations");
                return mapper.readerForListOf(Recommendation.class).readValue(recList);
            } catch (Exception e) {
                throw new RuntimeException("LLM 추천 결과 파싱 실패", e);
            }
        }
        return List.of();

    }
}

