package com.itplace.userapi.favorite.service;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.entity.enums.MainCategory;
import com.itplace.userapi.partner.entity.Partner;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.favorite.dto.FavoriteDetailResponseDto;
import com.itplace.userapi.favorite.dto.FavoriteRequestDto;
import com.itplace.userapi.favorite.dto.FavoriteResponseDto;
import com.itplace.userapi.favorite.dto.TierBenefitDetailDto;
import com.itplace.userapi.favorite.entity.Favorite;
import com.itplace.userapi.favorite.repository.FavoriteRepository;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final BenefitRepository benefitRepository;

    @Override
    public void addFavorite(FavoriteRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없어요"));
        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new IllegalArgumentException("혜택 및 제휴처를 찾을 수 없어요"));

        if (favoriteRepository.existsByUserAndBenefit(user, benefit)) {
            throw new IllegalStateException("이미 존재하는 즐겨찾기 항목입니다!");
        }

        Favorite favorite = Favorite.builder()
                .user(user)
                .benefit(benefit)
                .build();

        favoriteRepository.save(favorite);
    }

    @Override
    public void removeFavorite(FavoriteRequestDto request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없어요"));
        Benefit benefit = benefitRepository.findById(request.getBenefitId())
                .orElseThrow(() -> new IllegalArgumentException("혜택 및 제휴처를 찾을 수 없어요"));

        favoriteRepository.deleteByUserAndBenefit(user, benefit);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<FavoriteResponseDto> getFavorites(Long userId, String category, Pageable pageable) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없어요"));

        Page<Favorite> favorites;

        if (category != null && !category.isBlank()) {
            MainCategory mainCategory = MainCategory.fromLabel(category);
            favorites = favoriteRepository.findByUserAndBenefit_MainCategory(user, mainCategory, pageable);
        } else {
            favorites = favoriteRepository.findByUser(user,pageable);
        }

        return favorites.map(fav -> {
                    Benefit benefit = fav.getBenefit();
                    Partner partner = benefit.getPartner();

                    return FavoriteResponseDto.builder()
                            .benefitId(benefit.getBenefitId())
                            .benefitName(benefit.getBenefitName())
                            .partnerName(partner != null ? partner.getPartnerName() : null)
                            .partnerImage(partner != null ? partner.getImage() : null)
                            .build();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<FavoriteResponseDto> searchFavorites(Long userId, String keyword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없어요"));

        return favoriteRepository.findByUserAndBenefit_BenefitNameContaining(user, keyword).stream()
                .map(fav -> {
                    Benefit benefit = fav.getBenefit();
                    Partner partner = benefit.getPartner();

                    return FavoriteResponseDto.builder()
                            .benefitId(benefit.getBenefitId())
                            .benefitName(benefit.getBenefitName())
                            .partnerName(partner != null ? partner.getPartnerName() : null)
                            .partnerImage(partner != null ? partner.getImage() : null)
                            .build();
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FavoriteDetailResponseDto getBenefitDetail(Long benefitId) {
        Benefit benefit = benefitRepository.findDetailById(benefitId)
                .orElseThrow(() -> new IllegalArgumentException("혜택을 찾을 수 없습니다."));

        Partner partner = benefit.getPartner();

        List<TierBenefitDetailDto> tierDtos = benefit.getTierBenefits().stream()
                .map(tb -> TierBenefitDetailDto.builder()
                        .grade(tb.getGrade())
                        .isAll(tb.getIsAll())
                        .context(tb.getContext())
                        .discountValue(tb.getDiscountValue())
                        .build())
                .collect(Collectors.toList());

        return FavoriteDetailResponseDto.builder()
                .benefitId(benefit.getBenefitId())
                .benefitName(benefit.getBenefitName())
                .benefitDescription(benefit.getDescription())
                .benefitLimit(benefit.getBenefitLimit())
                .partnerName(partner != null ? partner.getPartnerName() : null)
                .partnerImage(partner != null ? partner.getImage() : null)
                .tiers(tierDtos)
                .build();
    }


}
