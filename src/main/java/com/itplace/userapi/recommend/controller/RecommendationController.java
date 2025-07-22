package com.itplace.userapi.recommend.controller;

import com.itplace.userapi.recommend.dto.RecommendationResponse;
import com.itplace.userapi.recommend.service.AbstractRecommendationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/recommend")
@RequiredArgsConstructor
public class RecommendationController {
    private final AbstractRecommendationService recommendationService;

    @GetMapping
    public ResponseEntity<RecommendationResponse> getRecommendation(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "5") int topK
    ) {

        try {
            String recText = recommendationService.recommend(userId, topK);
            return ResponseEntity.ok(new RecommendationResponse(recText));
        } catch (Exception e) {
            String errorMsg = "추천 생성 중 오류: " + e.getMessage();
            return ResponseEntity.status(500).body(new RecommendationResponse(errorMsg));
        }
    }
}
