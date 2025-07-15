package com.itplace.userapi.favorite.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.favorite.dto.FavoriteDetailResponseDto;
import com.itplace.userapi.favorite.dto.FavoriteRequestDto;
import com.itplace.userapi.favorite.dto.FavoriteResponseDto;
import com.itplace.userapi.favorite.dto.PageResultDto;
import com.itplace.userapi.favorite.enums.FavoriteCode;
import com.itplace.userapi.favorite.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addFavorite(@RequestBody FavoriteRequestDto request) {
        favoriteService.addFavorite(request);
        return ResponseEntity.ok(ApiResponse.ok(FavoriteCode.FAVORITE_ADD_SUCCESS_200));
    };

    // 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@RequestBody FavoriteRequestDto request) {
        favoriteService.removeFavorite(request);
        return ResponseEntity.ok(ApiResponse.ok(FavoriteCode.FAVORITE_DELETE_SUCCESS_200));
    }

    // 즐겨찾기 목록 조회 (페이징,필터링)
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<PageResultDto<FavoriteResponseDto>>> getFavorites(
            @PathVariable Long userId,
            @RequestParam(required = false) String category,
            @PageableDefault(page = 0, size = 6) Pageable pageable) {

        Page<FavoriteResponseDto> page = favoriteService.getFavorites(userId, category, pageable);

        PageResultDto<FavoriteResponseDto> result = PageResultDto.of(page);

        return ResponseEntity.ok(ApiResponse.of(FavoriteCode.FAVORITE_BENEFIT_SUCCESS_200, result));
    }


    // 즐겨찾기 혜택 이름 검색
    @GetMapping("/{userId}/search")
    public ResponseEntity<ApiResponse<List<FavoriteResponseDto>>> searchFavorites(
            @PathVariable Long userId,
            @RequestParam String keyword) {
        List<FavoriteResponseDto> favorites = favoriteService.searchFavorites(userId, keyword);
        return ResponseEntity.ok(ApiResponse.of(FavoriteCode.FAVORITE_BENEFIT_SEARCH_SUCCESS_200,favorites));
    }

    // 즐겨찾기 혜택 상세
    @GetMapping("/benefits/{benefitId}")
    public ResponseEntity<ApiResponse<FavoriteDetailResponseDto>> getBenefitDetail(
            @PathVariable Long benefitId) {
        FavoriteDetailResponseDto detail = favoriteService.getBenefitDetail(benefitId);
        return ResponseEntity.ok(ApiResponse.of(FavoriteCode.FAVORITE_BENEFIT_DETAIL_SUCCESS_200, detail));
    }

}
