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

    private Map<Long, Integer> logBasedBenefitScores;     // 로그 기반 점수 (별도 유지)
    private List<String> logBasedPartnerNames;

    public String getLLMContext() {
        String categoryInfo = topCategories.isEmpty() ? "알 수 없음" : String.join(", ", topCategories);
        String benefitPartners = recentPartnerNames.isEmpty() ? "알 수 없음" : String.join(", ", recentPartnerNames);
        String logPartners = logBasedPartnerNames.isEmpty() ? "알 수 없음" : String.join(", ", logBasedPartnerNames);

        if (grade == null) {
            return String.format(
                    "이 사용자는 멤버십 등급 정보는 없지만, 클릭/검색/상세 기록을 보면 최근 '%s' 제휴사에 관심이 많았어요.",
                    logPartners
            );
        }

        return String.format(
                "이 사용자의 멤버십 등급은 '%s'이며, 최근 '%s' 카테고리에 관심이 많았고, 자주 이용한 제휴사는 '%s'입니다. "
                        + "또한 클릭/검색/상세 행동 기록을 보면 최근 '%s' 제휴사에 특히 관심이 많았어요.",
                grade, categoryInfo, benefitPartners, logPartners
        );

    }

    public String getEmbeddingText() {
        return String.format(
                """
                        이 사용자는 최근 '%s' 카테고리에 관심이 많고, '%s' 제휴사의 혜택을 자주 검색하거나 클릭했습니다.
                        """,
                String.join(", ", topCategories),
                String.join(", ", logBasedPartnerNames)
        );
    }


}
