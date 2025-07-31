package com.itplace.userapi.map.service;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.benefit.repository.TierBenefitRepository;
import com.itplace.userapi.map.StoreCode;
import com.itplace.userapi.map.dto.PartnerDto;
import com.itplace.userapi.map.dto.StoreDetailDto;
import com.itplace.userapi.map.dto.StoreDto;
import com.itplace.userapi.map.dto.TierBenefitDto;
import com.itplace.userapi.map.entity.Store;
import com.itplace.userapi.map.exception.StoreKeywordException;
import com.itplace.userapi.map.repository.StoreRepository;
import com.itplace.userapi.partner.PartnerCode;
import com.itplace.userapi.partner.entity.Partner;
import com.itplace.userapi.partner.exception.PartnerNotFoundException;
import com.itplace.userapi.partner.repository.PartnerRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class StoreServiceImpl implements StoreService {
    private final StoreRepository storeRepository;
    private final BenefitRepository benefitRepository;
    private final TierBenefitRepository tierBenefitRepository;
    private final PartnerRepository partnerRepository;

    @Override
    @Transactional(readOnly = true)
    public List<StoreDetailDto> findNearby(double lat, double lng, double radiusMeters) {
        // 지구 반지름 (미터)
        double earthRadius = 6378137.0;

        double dLat = radiusMeters / earthRadius;
        double dLng = radiusMeters / (earthRadius * Math.cos(Math.toRadians(lat)));

        double minLat = lat - Math.toDegrees(dLat);
        double maxLat = lat + Math.toDegrees(dLat);
        double minLng = lng - Math.toDegrees(dLng);
        double maxLng = lng + Math.toDegrees(dLng);

        List<Store> stores = storeRepository.findNearbyStores(lat, lng, radiusMeters, minLat, maxLat, minLng, maxLng);

        return stores.stream()
                .map(store -> {
                    Partner partner = store.getPartner();
                    double storeLat = store.getLocation().getY();
                    double storeLng = store.getLocation().getX();
                    double distance = calculateDistance(lat, lng, storeLat, storeLng);

                    List<Benefit> benefits = benefitRepository.findAllByPartner_PartnerId(partner.getPartnerId());

                    List<Benefit> finalBenefits = selectBenefits(benefits, store.getStoreName());

                    List<TierBenefitDto> tierBenefitDtos = finalBenefits.stream()
                            .flatMap(benefit ->
                                    tierBenefitRepository.findAllByBenefit_BenefitId(benefit.getBenefitId()).stream()
                                            .map(tierBenefit -> TierBenefitDto.builder()
                                                    .grade(tierBenefit.getGrade())
                                                    .context(tierBenefit.getContext())
                                                    .build())
                            )
                            .toList();
                    return StoreDetailDto.builder()
                            .store(StoreDto.builder()
                                    .storeId(store.getStoreId())
                                    .storeName(store.getStoreName())
                                    .business(store.getBusiness())
                                    .city(store.getCity())
                                    .town(store.getTown())
                                    .legalDong(store.getLegalDong())
                                    .address(store.getAddress())
                                    .roadName(store.getRoadName())
                                    .roadAddress(store.getRoadAddress())
                                    .postCode(store.getPostCode())
                                    .longitude(store.getLocation().getX())
                                    .latitude(store.getLocation().getY())
                                    .hasCoupon(store.isHasCoupon())
                                    .build())
                            .partner(PartnerDto.builder()
                                    .partnerId(partner.getPartnerId())
                                    .partnerName(partner.getPartnerName())
                                    .image(partner.getImage())
                                    .category(partner.getCategory().trim())
                                    .build())
                            .tierBenefit(tierBenefitDtos)
                            .distance(distance)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDetailDto> findNearbyByCategory(double lat, double lng, double radiusMeters, String category) {
        List<StoreDetailDto> allStores = findNearby(lat, lng, radiusMeters);

        // "전체", null, 빈 문자열이면 전체 반환
        if (category == null || category.isBlank() || category.equalsIgnoreCase("전체")) {
            return allStores;
        }

        return allStores.stream()
                .filter(storeDetailDto ->
                        storeDetailDto.getPartner() != null &&
                                category.equalsIgnoreCase(storeDetailDto.getPartner().getCategory()))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDetailDto> findNearbyByKeyword(double lat, double lng, String category,
                                                    String keyword) {

        if (keyword == null || keyword.isBlank()) {
            throw new StoreKeywordException(StoreCode.KEYWORD_REQUEST);
        }
        if (category != null && category.isBlank()) {
            category = null;
        }

        List<Store> stores = storeRepository.searchNearbyStores(lng, lat, category, keyword);

        return stores.stream()
                .map(store -> {
                    Partner partner = store.getPartner();
                    double storeLat = store.getLocation().getY();
                    double storeLng = store.getLocation().getX();
                    double distance = calculateDistance(lat, lng, storeLat, storeLng);

                    List<Benefit> benefits = benefitRepository.findAllByPartner_PartnerId(partner.getPartnerId());

                    List<Benefit> finalBenefits = selectBenefits(benefits, store.getStoreName());

                    List<TierBenefitDto> tierBenefitDtos = finalBenefits.stream()
                            .flatMap(benefit ->
                                    tierBenefitRepository.findAllByBenefit_BenefitId(benefit.getBenefitId()).stream()
                                            .map(tierBenefit -> TierBenefitDto.builder()
                                                    .grade(tierBenefit.getGrade())
                                                    .context(tierBenefit.getContext())
                                                    .build())
                            )
                            .toList();
                    return StoreDetailDto.builder()
                            .store(StoreDto.builder()
                                    .storeId(store.getStoreId())
                                    .storeName(store.getStoreName())
                                    .business(store.getBusiness())
                                    .city(store.getCity())
                                    .town(store.getTown())
                                    .legalDong(store.getLegalDong())
                                    .address(store.getAddress())
                                    .roadName(store.getRoadName())
                                    .roadAddress(store.getRoadAddress())
                                    .postCode(store.getPostCode())
                                    .longitude(store.getLocation().getX())
                                    .latitude(store.getLocation().getY())
                                    .hasCoupon(store.isHasCoupon())
                                    .build())
                            .partner(PartnerDto.builder()
                                    .partnerId(partner.getPartnerId())
                                    .partnerName(partner.getPartnerName())
                                    .image(partner.getImage())
                                    .category(partner.getCategory().trim())
                                    .build())
                            .tierBenefit(tierBenefitDtos)
                            .distance(distance)
                            .build();
                })
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<StoreDetailDto> findNearbyByPartnerName(double lat, double lng, String partnerName) {
        if (partnerName == null || partnerName.isBlank()) {
            throw new StoreKeywordException(StoreCode.PARTNERNAME_REQUEST);
        }

        // partnerName으로 partnerId 조회
        Partner partner = partnerRepository.findByPartnerName(partnerName)
                .orElseThrow(() -> new PartnerNotFoundException(PartnerCode.PARTNER_NOT_FOUND));

        // partnerId로 매장 검색
        List<Store> stores = storeRepository.searchNearbyStoresByPartnerId(lng, lat, partner.getPartnerId());

        return stores.stream()
                .map(store -> {
                    double storeLat = store.getLocation().getY();
                    double storeLng = store.getLocation().getX();
                    double distance = calculateDistance(lat, lng, storeLat, storeLng);

                    // 제휴사 혜택 조회
                    List<Benefit> benefits = benefitRepository.findAllByPartner_PartnerId(partner.getPartnerId());
                    List<Benefit> finalBenefits = selectBenefits(benefits, store.getStoreName());

                    // tierBenefit 매핑
                    List<TierBenefitDto> tierBenefitDtos = finalBenefits.stream()
                            .flatMap(benefit ->
                                    tierBenefitRepository.findAllByBenefit_BenefitId(benefit.getBenefitId()).stream()
                                            .map(tierBenefit -> TierBenefitDto.builder()
                                                    .grade(tierBenefit.getGrade())
                                                    .context(tierBenefit.getContext())
                                                    .build())
                            )
                            .toList();

                    return StoreDetailDto.builder()
                            .store(StoreDto.builder()
                                    .storeId(store.getStoreId())
                                    .storeName(store.getStoreName())
                                    .business(store.getBusiness())
                                    .city(store.getCity())
                                    .town(store.getTown())
                                    .legalDong(store.getLegalDong())
                                    .address(store.getAddress())
                                    .roadName(store.getRoadName())
                                    .roadAddress(store.getRoadAddress())
                                    .postCode(store.getPostCode())
                                    .longitude(store.getLocation().getX())
                                    .latitude(store.getLocation().getY())
                                    .hasCoupon(store.isHasCoupon())
                                    .build())
                            .partner(PartnerDto.builder()
                                    .partnerId(partner.getPartnerId())
                                    .partnerName(partner.getPartnerName())
                                    .image(partner.getImage())
                                    .category(partner.getCategory().trim())
                                    .build())
                            .tierBenefit(tierBenefitDtos)
                            .distance(distance)
                            .build();
                })
                .toList();
    }

    private double calculateDistance(double userLat, double userLng, double storeLat, double storeLng) {
        final int earthRadius = 6378137; // 미터

        double dLat = Math.toRadians(storeLat - userLat);
        double dLng = Math.toRadians(storeLng - userLng);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(userLat))
                * Math.cos(Math.toRadians(storeLat))
                * Math.sin(dLng / 2) * Math.sin(dLng / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        double d = earthRadius * c * 0.001; // km 단위 거리
        return Math.round(d * 10) / 10.0;
    }

    private List<Benefit> selectBenefits(List<Benefit> benefits, String storeName) {
        // 오프라인/온라인이 나뉘어져 있는 경우
        List<Benefit> offlineBenefit = benefits.stream()
                .filter(b -> b.getBenefitName().contains("오프라인"))
                .findFirst()
                .map(List::of)
                .orElse(null);

        if (offlineBenefit != null) {
            return offlineBenefit;
        }

        if (benefits.size() >= 3) {
            return benefits.stream()
                    .filter(b -> b.getBenefitName().equals(storeName))
                    .toList();
        }

        return benefits;
    }


}
