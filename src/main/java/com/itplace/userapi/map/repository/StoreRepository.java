package com.itplace.userapi.map.repository;

import com.itplace.userapi.map.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query(value = """
            SELECT s.*
              FROM (
                     SELECT
                       *,
                       -- POINT(x=lng, y=lat) + SRID 설정
                       ST_Distance_Sphere(
                         location,
                         ST_SRID(
                           POINT(:lng, :lat),
                           4326
                         )
                       ) AS distance
                     FROM store
                    WHERE longitude BETWEEN :minLng AND :maxLng
                      AND latitude  BETWEEN :minLat AND :maxLat
                      AND ST_Distance_Sphere(
                            location,
                            ST_SRID(
                              POINT(:lng, :lat),
                              4326
                            )
                          ) <= :radiusMeters
                   ) AS s
             ORDER BY RAND()    -- 반경 내에서 완전 랜덤
             LIMIT 150
            """,
            nativeQuery = true)
    List<Store> findNearbyStores(
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("radiusMeters") double radiusMeters
    );

    @Query(
            value = """
                     SELECT s.*, CASE WHEN s.storeName = :keyword THEN 1 ELSE 0 END AS is_exact,
                     (MATCH(s.storeName, s.business) AGAINST(:keyword IN NATURAL LANGUAGE MODE)
                     + MATCH(p.partnerName, p.category) AGAINST(:keyword IN NATURAL LANGUAGE MODE)) AS relevance,
                     ST_Distance_Sphere(s.location, ST_SRID(Point(:lng, :lat), 4326)) AS distance FROM store s
                    JOIN partner p ON s.partnerId = p.partnerId
                    WHERE s.location IS NOT NULL
                    AND (:category IS NULL OR p.category = :category)
                    AND (MATCH(s.storeName, s.business) AGAINST(CONCAT('+', :keyword, '*') IN BOOLEAN MODE)
                    OR MATCH(p.partnerName, p.category) AGAINST(CONCAT('+', :keyword, '*') IN BOOLEAN MODE))
                     ORDER BY is_exact DESC, distance ASC, relevance DESC
                     LIMIT 30
                    """,
            nativeQuery = true
    )
    List<Store> searchNearbyStores(@Param("lng") double lng, @Param("lat") double lat,
                                   @Param("category") String category, @Param("keyword") String keyword);

    Store findByStoreName(String storeName);

    @Query(
            value = """
                    SELECT s.*
                    FROM store s
                    WHERE s.location IS NOT NULL
                      AND s.partnerId = :partnerId
                    ORDER BY ST_Distance_Sphere(location, ST_SRID(Point(:lng, :lat),4326)) ASC
                    LIMIT 30
                    """,
            nativeQuery = true
    )
    List<Store> searchNearbyStoresByPartnerId(
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("partnerId") Long partnerId
    );

    @Query("""
                SELECT s FROM Store s
                JOIN FETCH s.partner p
                WHERE s.storeId = :storeId AND p.partnerId = :partnerId
            """)
    Optional<Store> findByIdAndPartnerId(
            @Param("storeId") Long storeId,
            @Param("partnerId") Long partnerId);
}
