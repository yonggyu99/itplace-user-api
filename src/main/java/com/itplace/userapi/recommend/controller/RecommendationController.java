package com.itplace.userapi.recommend.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.recommend.dto.Recommendation;
import com.itplace.userapi.recommend.enums.RecommendationCode;
import com.itplace.userapi.recommend.service.OpenAiRecommendationService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recommendations")
public class RecommendationController {

    private final OpenAiRecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<List<Recommendation>>> recommend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int topK) {

        try {
            List<Recommendation> result = recommendationService.recommend(userId, topK);
            return ResponseEntity.ok(ApiResponse.of(RecommendationCode.RECOMMENDATION_SUCCESS, result));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.of(RecommendationCode.RECOMMENDATION_FAIL, null));
        }
    }
}


