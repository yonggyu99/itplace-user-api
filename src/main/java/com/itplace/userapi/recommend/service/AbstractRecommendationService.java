package com.itplace.userapi.recommend.service;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.repository.BenefitRepository;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class AbstractRecommendationService {
    private static final int EXPIRED_DAYS = 3;
    protected final UserFeatureService ufService;
    protected final RetrievalStrategy retrievalStrategy;
    protected final RankingStrategy rankingStrategy;
    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final BenefitRepository benefitRepository;


    public List<Recommendations> recommend(Long userId, int topK) throws Exception {

        LocalDateTime threshold = LocalDateTime.now().minusDays(EXPIRED_DAYS);

        LocalDate latestRecommendationDate = recommendationRepository.findLatestRecommendationDate(userId, threshold);

        if (latestRecommendationDate != null) {
            List<Recommendation> saved = recommendationRepository
                    .findByUserIdAndCreatedDate(userId, latestRecommendationDate);
            if (!saved.isEmpty()) {
                return RecommendationMapper.toDtoList(saved);
            }
        }

        // 사용자 피처 추출
        UserFeature uf = ufService.loadUserFeature(userId);
        // 추천 리스트
        List<Candidate> cands = retrievalStrategy.retrieve(uf, 50);
        // 재랭킹 및 추천 이유 생성
        List<Recommendations> recommendations = rankingStrategy.rank(uf, cands, topK);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));

        List<Recommendation> entities = recommendations.stream()
                .map(dto -> {
                    List<Benefit> benefits = dto.getBenefitIds().stream()
                            .map(benefitRepository::getReferenceById)
                            .toList();
                    return RecommendationMapper.toEntity(dto, user, benefits);
                })
                .toList();

        recommendationRepository.saveAll(entities);

        return recommendations;
    }
}
