package com.itplace.userapi.favorite.repository;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.entity.enums.MainCategory;
import com.itplace.userapi.favorite.entity.Favorite;
import com.itplace.userapi.favorite.entity.FavoriteId;
import com.itplace.userapi.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, FavoriteId> {

    Page<Favorite> findByUser(User user, Pageable pageable);
    Page<Favorite> findByUserAndBenefit_MainCategory(User user, MainCategory mainCategory, Pageable pageable);

    @Query("""
    SELECT f FROM Favorite f
    JOIN FETCH f.benefit b
    JOIN FETCH b.partner
    WHERE f.user = :user
      AND b.benefitName LIKE %:keyword%
""")

    List<Favorite> findByUserAndBenefit_BenefitNameContaining(User user, String keyword);

    boolean existsByUserAndBenefit(User user, Benefit benefit);

    void deleteByUserAndBenefit(User user, Benefit benefit);
}
