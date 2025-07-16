package com.itplace.userapi.benefit.service.impl;

import com.itplace.userapi.benefit.dto.response.BenefitListResponse;
import com.itplace.userapi.benefit.dto.response.PagedResponse;
import com.itplace.userapi.benefit.dto.response.TierBenefitInfo;
import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.entity.TierBenefit;
import com.itplace.userapi.benefit.entity.enums.MainCategory;
import com.itplace.userapi.benefit.entity.enums.UsageType;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.benefit.repository.TierBenefitRepository;
import com.itplace.userapi.benefit.service.BenefitService;
import com.itplace.userapi.favorite.repository.FavoriteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BenefitServiceImpl implements BenefitService {

    private final TierBenefitRepository tierBenefitRepository;
    private final FavoriteRepository favoriteRepository;
    private final BenefitRepository benefitRepository;

    @Override
    public PagedResponse<BenefitListResponse> getBenefitList(
            MainCategory mainCategory,
            String category,
            UsageType filter,
            String keyword,
            Long userId,
            Pageable pageable
    ) {
        Page<Benefit> benefitPage = benefitRepository.findFilteredBenefits(
                mainCategory,
                category,
                filter != null ? filter.name() : null,
                keyword,
                pageable
        );

        List<Benefit> benefitList = benefitPage.getContent();
        List<Long> benefitIds = benefitList.stream()
                .map(Benefit::getBenefitId)
                .toList();

        // 모든 TierBenefit 한 번에 가져오기
        List<TierBenefit> allTierBenefits = tierBenefitRepository.findAllByBenefitIn(benefitList);
        Map<Long, List<TierBenefit>> benefitToTierMap = allTierBenefits.stream()
                .collect(Collectors.groupingBy(tb -> tb.getBenefit().getBenefitId()));

        // 즐겨찾기 여부를 한 번에 가져오기
        Set<Long> userFavoriteBenefitIds = new HashSet<>(
                favoriteRepository.findFavoriteBenefitIdsByUser(userId, benefitIds)
        );

        // 즐겨찾기 수를 한 번에 가져오기
        Map<Long, Long> favoriteCountMap = favoriteRepository.countFavoritesByBenefitIds(benefitIds).stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> (Long) row[1]
                ));

        // DTO 변환
        List<BenefitListResponse> result = benefitList.stream()
                .map(b -> {
                    Long benefitId = b.getBenefitId();
                    boolean isFavorite = userFavoriteBenefitIds.contains(benefitId);
                    long favoriteCount = favoriteCountMap.getOrDefault(benefitId, 0L);

                    List<TierBenefitInfo> tierBenefits = benefitToTierMap
                            .getOrDefault(benefitId, Collections.emptyList())
                            .stream()
                            .map(tb -> new TierBenefitInfo(tb.getGrade(), tb.getContext(), tb.getIsAll()))
                            .toList();

                    return BenefitListResponse.builder()
                            .benefitId(benefitId)
                            .benefitName(b.getBenefitName())
                            .mainCategory(b.getMainCategory())
                            .usageType(b.getUsageType())
                            .category(Optional.ofNullable(b.getPartner().getCategory()).map(String::trim).orElse(null))
                            .image(b.getPartner().getImage())
                            .isFavorite(isFavorite)
                            .favoriteCount(favoriteCount)
                            .tierBenefits(tierBenefits)
                            .build();
                })
                .toList();

        return new PagedResponse<>(
                result,
                benefitPage.getNumber(),
                benefitPage.getTotalPages(),
                benefitPage.getTotalElements(),
                benefitPage.hasNext()
        );
    }
}
