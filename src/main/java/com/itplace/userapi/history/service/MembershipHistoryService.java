package com.itplace.userapi.history.service;

import com.itplace.userapi.benefit.dto.response.PagedResponse;
import com.itplace.userapi.history.dto.MembershipHistoryResponse;
import java.time.LocalDate;
import org.springframework.data.domain.Pageable;

public interface MembershipHistoryService {
    PagedResponse<MembershipHistoryResponse> getUserHistory(
            Long userId,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    );
}

