package com.itplace.userapi.recommend.dto;

import java.util.List;
import java.util.Map;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserFeature {
    private Long userId;
    private Map<String, Integer> recentCategoryScores;
    private List<String> topCategories;
    private Map<Long, Integer> benefitUsageCounts;
}
