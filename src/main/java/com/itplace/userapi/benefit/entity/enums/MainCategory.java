package com.itplace.userapi.benefit.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum MainCategory {
    VIP_COCK("VIP 콕"),
    BASIC_BENEFIT("기본 혜택");

    private final String label;

    MainCategory(String label) { this.label = label; }

    public static MainCategory fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown mainCategory: " + label));
    }
}
