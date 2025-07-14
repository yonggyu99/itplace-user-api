package com.itplace.userapi.benefit.entity;

import com.itplace.userapi.benefit.entity.enums.*;
import com.itplace.userapi.common.BaseTimeEntity;
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
@Table(name = "benefit")
public class Benefit extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long benefitId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partnerId")
    private Partner partner;

    @Convert(converter = MainCategoryConverter.class)
    private MainCategory mainCategory;

    private String benefitName;

    @Convert(converter = BenefitTypeConverter.class)
    private BenefitType type;

    private String description;

    @Lob
    private String manual;

    private String benefitLimit;

    @Convert(converter = UsageTypeConverter.class)
    private UsageType usageType;

    @Column(length = 512)
    private String url;

}

