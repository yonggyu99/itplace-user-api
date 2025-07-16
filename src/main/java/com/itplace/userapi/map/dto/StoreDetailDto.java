package com.itplace.userapi.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Getter
@Setter
@Builder
public class StoreDetailDto {
    private StoreDto store;
    private PartnerDto partner;
    private List<TierBenefitDto> tierBenefit;
    private double distance;
}
