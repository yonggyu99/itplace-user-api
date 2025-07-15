package com.itplace.userapi.favorite.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Builder
@Data
public class FavoriteDetailResponseDto {
    private Long benefitId;
    private String benefitName;
    private String benefitDescription;
    private String benefitLimit;
    private String partnerName;
    private String partnerImage;
    private List<TierBenefitDetailDto> tiers;
}

