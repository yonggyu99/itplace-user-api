package com.itplace.userapi.benefit.entity;

import com.itplace.userapi.benefit.entity.enums.*;
import com.itplace.userapi.common.BaseTimeEntity;
import com.itplace.userapi.favorite.entity.Favorite;
import com.itplace.userapi.partner.entity.Partner;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

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

    @Lob
    private String description;

    @Lob
    private String manual;

    private String benefitLimit;

    @Convert(converter = UsageTypeConverter.class)
    private UsageType usageType;

    @Column(length = 512)
    private String url;

    // 일단 benefit 삭제 시 외래키 제약 위반 걸어줘서 오류 처리
    // 즐겨찾기 먼저 제거 -> 혜택 삭제
    @OneToMany(mappedBy = "benefit")
    private List<Favorite> favorites = new ArrayList<>();

    @OneToMany(mappedBy = "benefit")
    private List<TierBenefit> tierBenefits = new ArrayList<>();

}

