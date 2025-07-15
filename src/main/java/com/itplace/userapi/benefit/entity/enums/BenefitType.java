package com.itplace.userapi.benefit.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum BenefitType {
    FREE("증정"),
    DISCOUNT("할인");

    private final String label;

    BenefitType(String label) { this.label = label; }

    public static BenefitType fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown benefit type: " + label));
    }
}
