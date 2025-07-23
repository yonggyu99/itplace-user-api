package com.itplace.userapi.recommend.dto;

import lombok.Data;

@Data
public class Recommendation {
    private int rank;
    private String partnerName;
    private String reason;
}

