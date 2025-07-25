package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.repository.RecommendationRepository;
import com.itplace.userapi.recommend.strategy.RankingStrategy;
import com.itplace.userapi.recommend.strategy.RetrievalStrategy;
import com.itplace.userapi.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class OpenAiRecommendationService extends AbstractRecommendationService {

    public OpenAiRecommendationService(
            UserFeatureService ufService,
            @Qualifier("openAiRetrieval") RetrievalStrategy retrievalStrategy,
            @Qualifier("openAiRanking") RankingStrategy rankingStrategy,
            RecommendationRepository recommendationRepository,
            UserRepository userRepository
    ) {
        super(ufService, retrievalStrategy, rankingStrategy, recommendationRepository, userRepository);
    }
}

