package com.itplace.userapi.recommend.service;

import com.itplace.userapi.history.repository.MembershipHistoryRepository;
import com.itplace.userapi.recommend.dto.UserFeature;
import com.itplace.userapi.recommend.projection.BenefitCount;
import com.itplace.userapi.recommend.projection.CategoryCount;
import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserFeatureServiceImpl implements UserFeatureService {
    private final MembershipHistoryRepository historyRepo;
    private final UserRepository userRepo;

    public UserFeature loadUserFeature(Long userId) {
        LocalDateTime since = LocalDateTime.now().minusYears(1);
        User user = userRepo.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));
        String membershipId = user.getMembershipId();

        Map<String, Integer> catScores = historyRepo
                .countByPartnerCategorySince(membershipId, since).stream()
                .collect(Collectors.toMap(
                        CategoryCount::getCategory,
                        cc -> cc.getCnt().intValue()
                ));
        Map<Long, Integer> benefitUsage = historyRepo
                .countByBenefitSince(membershipId, since).stream()
                .collect(Collectors.toMap(
                        BenefitCount::getBenefitId,
                        bc -> bc.getCnt().intValue()
                ));

        var topCats = catScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(3)
                .map(Map.Entry::getKey)
                .toList();

        return UserFeature.builder()
                .userId(userId)
                .recentCategoryScores(catScores)
                .topCategories(topCats)
                .benefitUsageCounts(benefitUsage)
                .build();

    }
}
