package com.itplace.userapi.history.entity;

import java.io.Serializable;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class MembershipHistoryId implements Serializable {
    private String membership;
    private Long benefit;
    private LocalDateTime usedAt;
}
