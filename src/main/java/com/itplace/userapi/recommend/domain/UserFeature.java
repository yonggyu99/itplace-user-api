package com.itplace.userapi.recommend.domain;

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
    private List<String> recentPartnerNames;

    public String getEmbeddingContext() {
        return String.format(
                "이 사용자는 최근 '%s' 카테고리에 관심이 많았고, '%s' 제휴사의 혜택을 자주 이용했습니다.",
                String.join(", ", topCategories),
                String.join(", ", recentPartnerNames)
        );
    }

    // LLM 재랭킹용 통계 컨텍스트 생성
    public String getLLMContext() {
        StringBuilder sb = new StringBuilder();
        
        sb.append("· 카테고리별 이용 횟수:\n");
        recentCategoryScores.entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .forEach(e -> sb.append(String.format("  - %s: %d회\n", e.getKey(), e.getValue())));

        sb.append("· 자주 이용한 제휴사:\n");
        recentPartnerNames.forEach(p -> sb.append("  - ").append(p).append("\n"));

        return sb.toString();
    }
}
