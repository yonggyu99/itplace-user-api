package com.itplace.userapi.favorite.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FavoriteResponseDto {
    private Long benefitId;
    private String benefitName;
    private String partnerName;
    private String partnerImage;
}
