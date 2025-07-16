package com.itplace.userapi.benefit.dto.response;

import com.itplace.userapi.benefit.entity.enums.MainCategory;
import com.itplace.userapi.benefit.entity.enums.UsageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BenefitListResponse {
    // Benefit
    private Long benefitId;
    private String benefitName;
    private MainCategory mainCategory;
    private UsageType usageType;

    // Partner
    private String category;
    private String image;

    // TierBenefit
    private List<TierBenefitInfo> tierBenefits;

    // Favorite
    private Boolean isFavorite;
    private Long favoriteCount;

}
