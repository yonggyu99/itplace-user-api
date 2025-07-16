package com.itplace.userapi.favorite.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.favorite.dto.FavoriteDetailResponse;
import com.itplace.userapi.favorite.dto.FavoriteRequest;
import com.itplace.userapi.favorite.dto.FavoriteResponse;
import com.itplace.userapi.favorite.dto.PageResult;
import com.itplace.userapi.favorite.enums.FavoriteCode;
import com.itplace.userapi.favorite.service.FavoriteService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    // 즐겨찾기 등록
    @PostMapping
    public ResponseEntity<ApiResponse<Void>> addFavorite(@RequestBody FavoriteRequest request) {
        favoriteService.addFavorite(request);
        ApiResponse<Void> body = ApiResponse.ok(FavoriteCode.FAVORITE_ADD_SUCCESS);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    // 즐겨찾기 삭제
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> removeFavorite(@RequestBody FavoriteRequest request) {
        favoriteService.removeFavorite(request);
        ApiResponse<Void> body = ApiResponse.ok(FavoriteCode.FAVORITE_DELETE_SUCCESS);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    // 즐겨찾기 목록 조회 (페이징, 필터링)
    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<PageResult<FavoriteResponse>>> getFavorites(
            @PathVariable Long userId,
            @RequestParam(required = false) String category,
            @PageableDefault(page = 0, size = 6) Pageable pageable) {

        Page<FavoriteResponse> page = favoriteService.getFavorites(userId, category, pageable);
        PageResult<FavoriteResponse> result = PageResult.of(page);
        ApiResponse<PageResult<FavoriteResponse>> body = ApiResponse.of(FavoriteCode.FAVORITE_BENEFIT_SUCCESS, result);

        return ResponseEntity.status(body.getStatus()).body(body);
    }

    // 즐겨찾기 혜택 이름 검색
    @GetMapping("/{userId}/search")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> searchFavorites(
            @PathVariable Long userId,
            @RequestParam String keyword) {

        List<FavoriteResponse> favorites = favoriteService.searchFavorites(userId, keyword);
        ApiResponse<List<FavoriteResponse>> body = ApiResponse.of(FavoriteCode.FAVORITE_BENEFIT_SEARCH_SUCCESS,
                favorites);

        return ResponseEntity.status(body.getStatus()).body(body);
    }

    // 즐겨찾기 혜택 상세
    @GetMapping("/benefits/{benefitId}")
    public ResponseEntity<ApiResponse<FavoriteDetailResponse>> getBenefitDetail(
            @PathVariable Long benefitId) {

        FavoriteDetailResponse detail = favoriteService.getBenefitDetail(benefitId);
        ApiResponse<FavoriteDetailResponse> body = ApiResponse.of(FavoriteCode.FAVORITE_BENEFIT_DETAIL_SUCCESS, detail);

        return ResponseEntity.status(body.getStatus()).body(body);
    }
}

