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
        if (grade == null) {
            // 콜드 스타트 사용자용 문장
            return String.format(
                    "이 사용자는 아직 멤버십 혜택을 이용하지 않았거나 멤버쉽 회원이 아니지만, 최근 '%s' 제휴사에 관심을 보였습니다.",
                    recentPartnerNames.isEmpty() ? "알 수 없음" : String.join(", ", recentPartnerNames)
            );
        }

        // 멤버십 사용자용 기본 문장
        return String.format(
                "이 사용자의 멤버십 등급은 '%s'이며, 최근 '%s' 카테고리에 관심이 많았고, '%s' 제휴사의 혜택을 자주 이용했습니다.",
                grade,
                topCategories.isEmpty() ? "알 수 없음" : String.join(", ", topCategories),
                recentPartnerNames.isEmpty() ? "알 수 없음" : String.join(", ", recentPartnerNames)
        );
    }

}
