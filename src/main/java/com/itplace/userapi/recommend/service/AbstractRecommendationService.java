package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.Recommendation;
import com.itplace.userapi.recommend.strategy.RankingStrategy;
import com.itplace.userapi.recommend.strategy.RetrievalStrategy;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRecommendationService {
    protected final UserFeatureService ufService;
    protected final RetrievalStrategy retrievalStrategy;
    protected final RankingStrategy rankingStrategy;

    public List<Recommendation> recommend(Long userId, int topK) throws Exception {
        // 사용자 피처 추출
        UserFeature uf = ufService.loadUserFeature(userId);
        // 추천 리스트
        List<Candidate> cands = retrievalStrategy.retrieve(uf, 50);
        // 재랭킹 및 추천 텍스트 생성
        List<Recommendation> recommendations = rankingStrategy.rank(uf, cands, topK);
        return recommendations;
    }
}
