package com.itplace.userapi.benefit.repository;

import com.itplace.userapi.benefit.entity.Benefit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    @Query("""
    SELECT b FROM Benefit b
    JOIN FETCH b.partner p
    LEFT JOIN FETCH b.tierBenefits tb
    WHERE b.benefitId = :benefitId
""")
    Optional<Benefit> findDetailById(@Param("benefitId") Long benefitId);

    List<Benefit> findAllByPartner_PartnerId(Long PartnerId);
}
