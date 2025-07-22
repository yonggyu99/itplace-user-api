package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.UserFeature;
import com.itplace.userapi.recommend.strategy.RankingStrategy;
import com.itplace.userapi.recommend.strategy.RetrievalStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRecommendationService {
    protected final UserFeatureService ufService;
    protected final RetrievalStrategy retrievalStrategy;
    protected final RankingStrategy rankingStrategy;

    public String recommend(Long userId, int topK) throws Exception {
        UserFeature uf = ufService.loadUserFeature(userId);
        List<Candidate> cands = retrievalStrategy.retrieve(uf, getCandidateSize());
        return rankingStrategy.rank(uf, cands, topK);
    }

    protected int getCandidateSize() {
        return 50;
    }
}
