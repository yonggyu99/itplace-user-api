package com.itplace.userapi.recommend.domain;

import com.itplace.userapi.benefit.entity.enums.Grade;
import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFeature {
    private Long userId;
    private Grade grade;
    private Map<String, Integer> recentCategoryScores;
    private List<String> topCategories;
    private Map<Long, Integer> benefitUsageCounts;
    private List<String> recentPartnerNames;

    public String getEmbeddingContext() {
        return String.format(
                "이 사용자의 등급은 '%s'이며, 최근 '%s' 카테고리에 관심이 많았고, '%s' 제휴사의 혜택을 자주 이용했습니다.",
                grade.toString(),
                String.join(", ", topCategories),
                String.join(", ", recentPartnerNames)
        );
    }
}
