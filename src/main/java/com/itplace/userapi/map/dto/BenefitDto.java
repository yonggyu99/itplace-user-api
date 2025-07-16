package com.itplace.userapi.map.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class BenefitDto {
    private Long benefitId;
    private Long partnerId;
}
