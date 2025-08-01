package com.itplace.userapi.map.repository;

import com.itplace.userapi.map.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query(value = """
            SELECT
                *, ST_Distance_Sphere(location, POINT(:lng, :lat)) AS distance
            FROM store
            WHERE
                latitude BETWEEN :minLat AND :maxLat
                AND longitude BETWEEN :minLng AND :maxLng
                AND ST_Distance_Sphere(location, POINT(:lng, :lat)) <= :radiusMeters
            ORDER BY distance
            """, nativeQuery = true)
    List<Store> findNearbyStores(
            @Param("lng") double lng,
            @Param("lat") double lat,
            @Param("radiusMeters") double radiusMeters,
            @Param("minLat") double minLat,
            @Param("maxLat") double maxLat,
            @Param("minLng") double minLng,
            @Param("maxLng") double maxLng
    );

    @Query(
            value = """
                    SELECT s.*
                    FROM store s
                    JOIN partner p ON s.partnerId = p.partnerId
                    WHERE s.location IS NOT NULL
                    AND (:category IS NULL OR p.category = :category)
                    AND (LOWER(s.storeName) LIKE CONCAT('%',LOWER(:keyword),'%') OR
                        LOWER(s.business) LIKE CONCAT('%',LOWER(:keyword),'%') OR
                        LOWER(p.partnerName) LIKE CONCAT('%',LOWER(:keyword),'%') OR
                        LOWER(p.category) LIKE CONCAT('%',LOWER(:keyword),'%')
                    )
                    ORDER BY ST_Distance_Sphere(location, ST_SRID(Point(:lng, :lat),4326)) ASC
                    LIMIT 30
                    """,
            nativeQuery = true
    )
    List<Store> searchNearbyStores(@Param("lng") double lng, @Param("lat") double lat,
                                   @Param("category") String category, @Param("keyword") String keyword);

    @Query("SELECT s FROM Store s JOIN FETCH s.partner WHERE s.storeId = :storeId")
    Optional<Store> findByIdWithPartner(@Param("storeId") Long storeId);

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
}
