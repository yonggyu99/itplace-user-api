package com.itplace.userapi.benefit.repository;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.entity.enums.MainCategory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BenefitRepository extends JpaRepository<Benefit, Long> {
    @Query("""
                SELECT b FROM Benefit b
                JOIN FETCH b.partner p
                LEFT JOIN FETCH b.tierBenefits tb
                WHERE b.benefitId = :benefitId
            """)
    Optional<Benefit> findDetailById(@Param("benefitId") Long benefitId);

    List<Benefit> findAllByPartner_PartnerId(Long PartnerId);

    @Query("""
                SELECT b
                FROM Benefit b
                LEFT JOIN FETCH b.partner p
                LEFT JOIN Favorite f ON f.benefit = b
                WHERE b.mainCategory = :mainCategory
                  AND (:category IS NULL OR
                       REPLACE(REPLACE(p.category, CHAR(13), ''), CHAR(10), '') =
                       REPLACE(REPLACE(:category, CHAR(13), ''), CHAR(10), '')
                  )
                  AND (
                       :filter IS NULL OR
                       (:filter = 'ONLINE' AND b.usageType IN ('ONLINE', 'BOTH')) OR
                       (:filter = 'OFFLINE' AND b.usageType IN ('OFFLINE', 'BOTH'))
                  )
                  AND (:keyword IS NULL OR LOWER(b.benefitName) LIKE LOWER(CONCAT('%', :keyword, '%')))
                GROUP BY b
                ORDER BY COUNT(f) DESC
            """)
    Page<Benefit> findFilteredBenefits(
            @Param("mainCategory") MainCategory mainCategory,
            @Param("category") String category,
            @Param("filter") String filter,
            @Param("keyword") String keyword,
            Pageable pageable
    );

    @Query("""
                SELECT b
                FROM Benefit b
                JOIN FETCH b.partner p
                WHERE b.benefitId = :benefitId
            """)
    Optional<Benefit> findBenefitWithPartnerById(@Param("benefitId") Long benefitId);

    List<Benefit> findByPartner_PartnerIdAndMainCategory(Long partnerId, MainCategory mainCategory);

}
