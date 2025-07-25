package com.itplace.userapi.recommend.mapper;

import com.itplace.userapi.recommend.dto.Recommendations;
import com.itplace.userapi.recommend.entity.Recommendation;
import com.itplace.userapi.user.entity.User;
import java.util.List;

public class RecommendationMapper {
    public static Recommendation toEntity(Recommendations dto, User user) {
        return Recommendation.builder()
                .user(user)
                .rank(dto.getRank())
                .partnerName(dto.getPartnerName())
                .reason(dto.getReason())
                .imgUrl(dto.getImgUrl())
                .build();
    }

    public static List<Recommendation> toEntityList(List<Recommendations> dtos, User user) {
        return dtos.stream()
                .map(dto -> toEntity(dto, user))
                .toList();
    }
}

