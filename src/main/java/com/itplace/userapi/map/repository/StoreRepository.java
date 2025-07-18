package com.itplace.userapi.map.repository;

import com.itplace.userapi.map.entity.Store;
import io.lettuce.core.dynamic.annotation.Param;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface StoreRepository extends JpaRepository<Store, Long> {

    @Query(
            value = """
                    SELECT *
                    FROM store
                    WHERE
                      longitude BETWEEN :minLng AND :maxLng
                      AND latitude BETWEEN :minLat AND :maxLat
                      AND ST_Distance_Sphere(
                            location,
                            ST_GeomFromText(CONCAT('POINT(', :lng, ' ', :lat, ')'), 4326)
                          ) <= :radiusMeters
                    ORDER BY ST_Distance_Sphere(
                            location,
                            ST_GeomFromText(CONCAT('POINT(', :lng, ' ', :lat, ')'), 4326)
                          )
                    """,
            nativeQuery = true
    )
    List<Store> findNearbyStores(
            @Param("lng") double lng, @Param("lat") double lat, @Param("radiusMeters") double radiusMeters,
            @Param("minLat") double minLat, @Param("maxLat") double maxLat,
            @Param("minLng") double minLng, @Param("maxLng") double maxLng
    );

    @Query("SELECT s FROM Store s JOIN FETCH s.partner WHERE s.storeId = :storeId")
    Optional<Store> findByIdWithPartner(@Param("storeId") Long storeId);
}
