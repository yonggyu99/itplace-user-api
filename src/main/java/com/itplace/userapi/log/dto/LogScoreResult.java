package com.itplace.userapi.log.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogScoreResult {
    @Field("_id.benefitId")
    private Long benefitId;

    @Field("_id.partnerName")
    private String partnerName;

    private int totalScore;
}

