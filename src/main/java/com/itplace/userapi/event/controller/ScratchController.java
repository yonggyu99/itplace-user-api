package com.itplace.userapi.event.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.event.ScratchCode;
import com.itplace.userapi.event.dto.ScratchResult;
import com.itplace.userapi.event.service.ScratchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/scratch")
@RequiredArgsConstructor
public class ScratchController {

    private final ScratchService scratchService;

    @PostMapping
    public ResponseEntity<ApiResponse<ScratchResult>> scratch() {
        Long userId = 1636L; // 나중에 principal로 대체
        ScratchResult result = scratchService.scratch(userId);

        ScratchCode code;
        if (!result.isSuccess() && "별이 부족합니다. 별을 다시 모은 후 시도해주세요.".equals(result.getMessage())) {
            code = ScratchCode.COUPON_LACK;
        } else {
            code = ScratchCode.SCRATCH_SUCCESS;
        }

        ApiResponse<ScratchResult> response = ApiResponse.of(code, result);
        return new ResponseEntity<>(response, code.getStatus());
    }
}
