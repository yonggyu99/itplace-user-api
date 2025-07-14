package com.itplace.userapi.benefit.entity.enums;

import lombok.Getter;

import java.util.Arrays;

@Getter
public enum UsageType {
    OFFLINE("offline"),
    ONLINE("online"),
    BOTH("both");

    private final String label;

    UsageType(String label) { this.label = label; }

    public static UsageType fromLabel(String label) {
        return Arrays.stream(values())
                .filter(c -> c.label.equals(label))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unknown usage type: " + label));
    }
}
