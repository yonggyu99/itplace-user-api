package com.itplace.userapi.benefit.repository;

import com.itplace.userapi.benefit.entity.TierBenefit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TierBenefitRepository extends JpaRepository<TierBenefit, Long> {
    List<TierBenefit> findAllByBenefit_BenefitId(Long benefitId);
}
