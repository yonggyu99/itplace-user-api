package com.itplace.userapi.history.entity;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.user.entity.Membership;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@IdClass(MembershipHistoryId.class)
@Table(name = "membershiphistory")
public class MembershipHistory {

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "membershipId")
    private Membership membership;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefitId")
    private Benefit benefit;

    @Id
    private LocalDateTime usedAt;

    private Long discountAmount;
}
