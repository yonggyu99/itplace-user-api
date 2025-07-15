package com.itplace.userapi.favorite.service;

import com.itplace.userapi.favorite.dto.FavoriteDetailResponseDto;
import com.itplace.userapi.favorite.dto.FavoriteRequestDto;
import com.itplace.userapi.favorite.dto.FavoriteResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FavoriteService {
    void addFavorite(FavoriteRequestDto request);
    void removeFavorite(FavoriteRequestDto request);
    Page<FavoriteResponseDto> getFavorites(Long userId, String category, Pageable pageable);
    List<FavoriteResponseDto> searchFavorites(Long userId, String keyword);
    FavoriteDetailResponseDto getBenefitDetail(Long benefitId);
}