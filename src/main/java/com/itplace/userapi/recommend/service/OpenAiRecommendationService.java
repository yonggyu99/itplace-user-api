package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.strategy.RankingStrategy;
import com.itplace.userapi.recommend.strategy.RetrievalStrategy;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OpenAiRecommendationService extends AbstractRecommendationService {
    public OpenAiRecommendationService(
            UserFeatureService ufService,
            @Qualifier("openAiRetrieval") RetrievalStrategy retrievalStrategy,
            @Qualifier("openAiRanking") RankingStrategy rankingStrategy
    ) {
        super(ufService, retrievalStrategy, rankingStrategy);
    }
}
