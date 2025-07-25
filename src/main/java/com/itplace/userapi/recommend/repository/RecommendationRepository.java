package com.itplace.userapi.recommend.repository;

import com.itplace.userapi.recommend.entity.Recommendation;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendationRepository extends JpaRepository<Recommendation, Long> {
    List<Recommendation> findByUser_IdAndCreatedDateAfterOrderByRankAsc(Long userId, LocalDateTime after);
}
