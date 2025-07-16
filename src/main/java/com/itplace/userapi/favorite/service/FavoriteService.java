package com.itplace.userapi.favorite.service;

import com.itplace.userapi.favorite.dto.FavoriteDetailResponse;
import com.itplace.userapi.favorite.dto.FavoriteRequest;
import com.itplace.userapi.favorite.dto.FavoriteResponse;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FavoriteService {
    void addFavorite(FavoriteRequest request);

    void removeFavorite(FavoriteRequest request);

    Page<FavoriteResponse> getFavorites(Long userId, String category, Pageable pageable);

    List<FavoriteResponse> searchFavorites(Long userId, String keyword);

    FavoriteDetailResponse getBenefitDetail(Long benefitId);
}