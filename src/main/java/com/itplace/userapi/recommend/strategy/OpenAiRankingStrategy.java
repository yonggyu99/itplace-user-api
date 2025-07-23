package com.itplace.userapi.recommend.strategy;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.Recommendation;
import com.itplace.userapi.recommend.service.OpenAIService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("openAiRanking")
@RequiredArgsConstructor
public class OpenAiRankingStrategy implements RankingStrategy {
    private final OpenAIService aiService;
    private final ObjectMapper mapper;

    @Override
    public List<Recommendation> rank(UserFeature uf, List<Candidate> cands, int topK) {
        return aiService.rerankAndExplain(uf, cands, topK);
    }
}
