package com.itplace.userapi.benefit.entity;

import com.itplace.userapi.benefit.entity.enums.Grade;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "tierbenefit")
@IdClass(TierBenefitId.class)
public class TierBenefit {
    @Id
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "benefitId", nullable = false)
    private Benefit benefit;

    @Column(nullable = false)
    private String context;

    private Boolean isAll;

    private Integer discountValue;
}
