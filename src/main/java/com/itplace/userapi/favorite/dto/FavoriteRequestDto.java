package com.itplace.userapi.favorite.dto;

import lombok.Data;

@Data
public class FavoriteRequestDto {
    private Long userId;
    private Long benefitId;
}
