package com.itplace.userapi.benefit.controller;

import com.itplace.userapi.benefit.BenefitCode;
import com.itplace.userapi.benefit.dto.response.BenefitDetailResponse;
import com.itplace.userapi.benefit.dto.response.BenefitListResponse;
import com.itplace.userapi.benefit.dto.response.MapBenefitDetailResponse;
import com.itplace.userapi.benefit.dto.response.PagedResponse;
import com.itplace.userapi.benefit.entity.enums.MainCategory;
import com.itplace.userapi.benefit.entity.enums.UsageType;
import com.itplace.userapi.benefit.service.BenefitService;
import com.itplace.userapi.common.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1/benefit")
@RestController
@RequiredArgsConstructor
public class BenefitController {
    private final BenefitService benefitService;

    @GetMapping
    public ResponseEntity<ApiResponse<PagedResponse<BenefitListResponse>>> getBenefits(
            @RequestParam MainCategory mainCategory,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) UsageType filter,
            @RequestParam(required = false, defaultValue = "POPULARITY") String sort,
            @RequestParam(required = false) String keyword,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size
    ) {

        Pageable pageable = PageRequest.of(page, size);
        PagedResponse<BenefitListResponse> result = benefitService.getBenefitList(
                mainCategory, category, filter, keyword, userId, pageable
        );
        ApiResponse<PagedResponse<BenefitListResponse>> body = ApiResponse.of(BenefitCode.BENEFIT_LIST_SUCCESS, result);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @GetMapping("/{benefitId}")
    public ResponseEntity<ApiResponse<BenefitDetailResponse>> getBenefitDetail(@PathVariable Long benefitId) {
        BenefitDetailResponse result = benefitService.getBenefitDetail(benefitId);
        ApiResponse<BenefitDetailResponse> body = ApiResponse.of(BenefitCode.BENEFIT_DETAIL_SUCCESS, result);
        return ResponseEntity.status(body.getStatus()).body(body);
    }

    @GetMapping("/map-detail")
    public ResponseEntity<ApiResponse<MapBenefitDetailResponse>> getMapBenefitDetail(
            @RequestParam Long storeId,
            @RequestParam Long partnerId,
            @RequestParam MainCategory mainCategory
    ) {
        MapBenefitDetailResponse detail = benefitService.getMapBenefitDetail(storeId, partnerId, mainCategory);
        ApiResponse<MapBenefitDetailResponse> body = ApiResponse.of(BenefitCode.BENEFIT_DETAIL_SUCCESS, detail);
        return ResponseEntity.status(body.getStatus()).body(body);
    }
}