package com.itplace.userapi.map.service;

import com.itplace.userapi.map.dto.StoreDetailDto;
import java.util.List;

public interface StoreService {
    List<StoreDetailDto> findNearby(double lat, double lng, double radiusMeters);

    List<StoreDetailDto> findNearbyByCategory(double lat, double lng, double radiusMeters, String category);

    List<StoreDetailDto> findNearbyByKeyword(double lat, double lng, double radiusMeters, String category,
                                             String keyword);
}
