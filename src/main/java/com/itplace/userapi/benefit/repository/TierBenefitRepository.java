package com.itplace.userapi.benefit.repository;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.entity.TierBenefit;
import com.itplace.userapi.benefit.entity.TierBenefitId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TierBenefitRepository extends JpaRepository<TierBenefit, TierBenefitId> {
    List<TierBenefit> findAllByBenefitIn(List<Benefit> benefits);
}
