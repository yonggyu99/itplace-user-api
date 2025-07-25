package com.itplace.userapi.recommend.strategy;

import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.Recommendations;
import com.itplace.userapi.recommend.service.OpenAIService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("openAiRanking")
@RequiredArgsConstructor
public class OpenAiRankingStrategy implements RankingStrategy {
    private final OpenAIService aiService;

    @Override
    public List<Recommendations> rank(UserFeature uf, List<Candidate> cands, int topK) {
        return aiService.rerankAndExplain(uf, cands, topK);
    }
}
