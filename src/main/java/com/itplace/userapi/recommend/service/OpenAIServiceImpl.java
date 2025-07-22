package com.itplace.userapi.recommend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.partner.entity.Partner;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.ChatCompletionResponse;
import com.itplace.userapi.recommend.dto.UserFeature;
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
    private final BenefitRepository benefitRepo;
    private final ObjectMapper mapper;

    // RestTemplate 으로만 호출하도록 변경
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${spring.ai.openai.api.key}")
    private String apiKey;

    @Value("${spring.ai.openai.api.url}")     // ex. https://api.openai.com
    private String baseUrl;

    @Value("${spring.ai.openai.model.chat}")       // ex. gpt-3.5-turbo
    private String model;


    @Override
    public List<Candidate> vectorSearch(UserFeature uf, int topK) {
        return uf.getBenefitUsageCounts().entrySet().stream()
                .sorted(Map.Entry.<Long, Integer>comparingByValue().reversed())
                .limit(topK)
                .map(e -> {
                    Benefit b = benefitRepo.findById(e.getKey())
                            .orElseThrow(() -> new IllegalArgumentException("혜택이 없습니다: " + e.getKey()));
                    Partner p = b.getPartner();
                    return Candidate.builder()
                            .benefitId(b.getBenefitId())
                            .benefitName(b.getBenefitName())
                            .partnerName(p.getPartnerName())
                            .category(p.getCategory())
                            .description(b.getDescription())
                            .build();
                })
                .toList();
    }

    /**
     * RestTemplate 으로 ChatCompletion 호출
     */
    @Override
    public String rerankAndExplain(UserFeature uf, List<Candidate> cands, int topK) {
        String url = baseUrl + "/v1/chat/completions";

        // 1) HTTP 헤더 준비
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 2) 후보 리스트 문자열화
        StringBuilder items = new StringBuilder();
        for (int i = 0; i < cands.size(); i++) {
            Candidate c = cands.get(i);
            items.append(String.format(
                    "%d. %s｜%s%n",
                    i + 1, c.getPartnerName(), c.getCategory()
            ));
        }

        // 3) 프롬프트 구성
        String prompt = String.format("""
                        【유저 요약】
                        Top Categories: %s
                        
                        【후보 %d개】
                        %s
                        
                        위 중 TOP-%d와 추천 이유를
                        ┌순위—제휴사명—추천 이유
                        └형식으로 뽑아주세요.
                        """,
                uf.getTopCategories(),
                cands.size(),
                items,
                topK
        );

        // 3) 시스템 메시지에 캐릭터 말투 지시 추가
        List<Map<String, String>> messages = List.of(
                Map.of(
                        "role", "system",
                        "content", "당신은 우주를 여행하는 귀여운 토끼 로봇 '바니봇'이에요! 말투는 언제나 밝고 상냥하게, '~한 걸요!','~한다냥!' 같은 어미를 사용하세요."
                ),
                Map.of("role", "user", "content", prompt)
        );

        // 4) 요청 바디 생성
        Map<String, Object> body = Map.of(
                "model", model,
                "messages", List.of(Map.of("role", "user", "content", prompt))
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        // 5) 호출 및 파싱
        ResponseEntity<ChatCompletionResponse> response = restTemplate
                .exchange(url, HttpMethod.POST, request, ChatCompletionResponse.class);

        ChatCompletionResponse cr = response.getBody();
        if (cr != null && !cr.getChoices().isEmpty()) {
            return cr.getChoices().get(0).getMessage().getContent();
        }
        return "추천 결과를 가져오지 못했습니다.";
    }
}

