package com.itplace.userapi.history.repository;

import com.itplace.userapi.history.entity.MembershipHistory;
import java.time.LocalDateTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface MembershipHistoryRepository extends JpaRepository<MembershipHistory, Long> {

    @Query("""
                SELECT mh
                FROM MembershipHistory mh
                JOIN FETCH mh.benefit b
                JOIN FETCH b.partner p
                WHERE mh.membership.membershipId = :membershipId
                  AND (:keyword IS NULL OR LOWER(b.benefitName) LIKE LOWER(CONCAT('%', :keyword, '%')))
                  AND (:startDate IS NULL OR mh.usedAt >= :startDate)
                  AND (:endDate IS NULL OR mh.usedAt <= :endDate)
                ORDER BY mh.usedAt DESC
            """)
    Page<MembershipHistory> findFiltered(
            @Param("membershipId") String membershipId,
            @Param("keyword") String keyword,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable
    );
}
