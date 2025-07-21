package com.itplace.userapi.benefit.service.impl;

import com.itplace.userapi.benefit.BenefitCode;
import com.itplace.userapi.benefit.dto.response.BenefitDetailResponse;
import com.itplace.userapi.benefit.dto.response.BenefitListResponse;
import com.itplace.userapi.benefit.dto.response.MapBenefitDetailResponse;
import com.itplace.userapi.benefit.dto.response.PagedResponse;
import com.itplace.userapi.benefit.dto.response.TierBenefitInfo;
import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.entity.TierBenefit;
import com.itplace.userapi.benefit.entity.enums.MainCategory;
import com.itplace.userapi.benefit.entity.enums.UsageType;
import com.itplace.userapi.benefit.exception.BenefitNotFoundException;
import com.itplace.userapi.benefit.exception.BenefitOfflineNotFoundException;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.benefit.repository.TierBenefitRepository;
import com.itplace.userapi.benefit.service.BenefitService;
import com.itplace.userapi.favorite.repository.FavoriteRepository;
import com.itplace.userapi.map.StoreCode;
import com.itplace.userapi.map.entity.Store;
import com.itplace.userapi.map.exception.StoreNotFoundException;
import com.itplace.userapi.map.exception.StorePartnerMismatchException;
import com.itplace.userapi.map.repository.StoreRepository;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BenefitServiceImpl implements BenefitService {

    private final TierBenefitRepository tierBenefitRepository;
    private final FavoriteRepository favoriteRepository;
    private final BenefitRepository benefitRepository;
    private final StoreRepository storeRepository;

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
        Set<Long> userFavoriteBenefitIds = (userId != null)
                ? new HashSet<>(favoriteRepository.findFavoriteBenefitIdsByUser(userId, benefitIds))
                : Collections.emptySet();

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
                            .partnerId(b.getPartner().getPartnerId())
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

    @Override
    public BenefitDetailResponse getBenefitDetail(Long benefitId) {
        Benefit benefit = benefitRepository.findBenefitWithPartnerById(benefitId)
                .orElseThrow(() -> new BenefitNotFoundException(BenefitCode.BENEFIT_NOT_FOUND));

        return BenefitDetailResponse.builder()
                .benefitId(benefit.getBenefitId())
                .benefitName(benefit.getBenefitName())
                .description(benefit.getDescription())
                .benefitLimit(benefit.getBenefitLimit().trim())
                .manual(benefit.getManual().trim())
                .url(benefit.getUrl().trim())
                .partnerName(benefit.getPartner().getPartnerName())
                .image(benefit.getPartner().getImage())
                .build();
    }

    @Override
    public MapBenefitDetailResponse getMapBenefitDetail(Long storeId, Long partnerId, MainCategory mainCategory) {
        log.info("[getMapBenefitDetail] storeId: {}, partnerId: {}, mainCategory: {}", storeId, partnerId,
                mainCategory);

        Store store = storeRepository.findByIdWithPartner(storeId)
                .orElseThrow(() -> new StoreNotFoundException(StoreCode.STORE_NOT_FOUND));

        if (!store.getPartner().getPartnerId().equals(partnerId)) {
            throw new StorePartnerMismatchException(StoreCode.STORE_PARTNER_MISMATCH);
        }

//        String storeName = normalize(store.getStoreName());

        List<Benefit> benefits = benefitRepository.findByPartner_PartnerIdAndMainCategory(partnerId, mainCategory);
        log.info("[benefits size] found: {}", benefits.size());

        if (benefits.isEmpty()) {
            throw new BenefitNotFoundException(BenefitCode.BENEFIT_NOT_FOUND);
        }

        Benefit selectedBenefit;

        if (benefits.size() == 1) {
            selectedBenefit = benefits.get(0);

        } else {
            selectedBenefit = benefits.stream()
                    .filter(b -> b.getUsageType() == UsageType.OFFLINE || b.getUsageType() == UsageType.BOTH)
                    .findFirst()
                    .orElseThrow(() -> new BenefitOfflineNotFoundException(BenefitCode.BENEFIT_OFFLINE_NOT_FOUND));
        }

//        if (benefits.size() == 1) {
//            selectedBenefit = benefits.get(0);
//
//        } else if (benefits.size() == 2) {
//            selectedBenefit = benefits.stream()
//                    .filter(b -> b.getUsageType() == UsageType.OFFLINE || b.getUsageType() == UsageType.BOTH)
//                    .findFirst()
//                    .orElseThrow(() -> new BenefitOfflineNotFoundException(BenefitCode.BENEFIT_OFFLINE_NOT_FOUND));
//        } else {
//            selectedBenefit = benefits.stream()
//                    .filter(b -> normalize(b.getBenefitName()).equals(storeName))
//                    .findFirst()
//                    .orElseThrow(() -> new BenefitNotFoundException(BenefitCode.BENEFIT_NOT_FOUND));
//        }

        log.info("[selectedBenefit] id: {}, name: {}", selectedBenefit.getBenefitId(),
                selectedBenefit.getBenefitName());

        List<TierBenefitInfo> tierDtos = tierBenefitRepository.findAllByBenefit_BenefitId(
                        selectedBenefit.getBenefitId()).stream()
                .map(tb -> new TierBenefitInfo(tb.getGrade(), tb.getContext(), tb.getIsAll()))
                .toList();

        return MapBenefitDetailResponse.builder()
                .benefitId(selectedBenefit.getBenefitId())
                .benefitName(selectedBenefit.getBenefitName())
                .mainCategory(selectedBenefit.getMainCategory())
                .manual(selectedBenefit.getManual())
                .url(selectedBenefit.getUrl().trim())
                .tierBenefits(tierDtos)
                .build();
    }

//    private String normalize(String input) {
//        return input.replaceAll("\\s+", "").toLowerCase();
//    }

}
