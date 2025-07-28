package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.Recommendations;
import com.itplace.userapi.recommend.entity.Recommendation;
import com.itplace.userapi.recommend.mapper.RecommendationMapper;
import com.itplace.userapi.recommend.repository.RecommendationRepository;
import com.itplace.userapi.recommend.strategy.RankingStrategy;
import com.itplace.userapi.recommend.strategy.RetrievalStrategy;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRecommendationService {
    private static final int EXPIRED_DAYS = 7;
    protected final UserFeatureService ufService;
    protected final RetrievalStrategy retrievalStrategy;
    protected final RankingStrategy rankingStrategy;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;


    public List<Recommendations> recommend(Long userId, int topK) throws Exception {
//         최근 1주일 이내 추천 이력 확인
        LocalDateTime threshold = LocalDateTime.now().minusDays(EXPIRED_DAYS);
        List<Recommendation> saved = recommendationRepository
                .findByUser_IdAndCreatedDateAfterOrderByRankAsc(userId, threshold);
        if (!saved.isEmpty()) {
            return saved.stream()
                    .map(e -> Recommendations.builder()
                            .rank(e.getRank())
                            .partnerName(e.getPartnerName())
                            .reason(e.getReason())
                            .imgUrl(e.getImgUrl())
                            .build())
                    .toList();
        }

        // 사용자 피처 추출
        UserFeature uf = ufService.loadUserFeature(userId);
        // 추천 리스트
        List<Candidate> cands = retrievalStrategy.retrieve(uf, 50);
        // 재랭킹 및 추천 이유 생성
        List<Recommendations> recommendations = rankingStrategy.rank(uf, cands, topK);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));

        List<Recommendation> entities = RecommendationMapper.toEntityList(recommendations, user);
        recommendationRepository.saveAll(entities);

        return recommendations;
    }
}
