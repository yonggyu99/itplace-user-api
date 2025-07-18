package com.itplace.userapi.history.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipHistoryId implements Serializable {
    private String membership;
    private Long benefit;
    private LocalDateTime usedAt;
}
