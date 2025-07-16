package com.itplace.userapi.favorite.dto;

import lombok.Data;

@Data
public class FavoriteRequest {
    private Long userId;
    private Long benefitId;
}
