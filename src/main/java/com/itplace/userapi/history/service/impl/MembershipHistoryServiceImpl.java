package com.itplace.userapi.history.service.impl;

import com.itplace.userapi.benefit.dto.response.PagedResponse;
import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.history.dto.MembershipHistoryResponse;
import com.itplace.userapi.history.dto.MonthlyDiscountResponse;
import com.itplace.userapi.history.entity.MembershipHistory;
import com.itplace.userapi.history.repository.MembershipHistoryRepository;
import com.itplace.userapi.history.service.MembershipHistoryService;
import com.itplace.userapi.partner.entity.Partner;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.user.UserCode;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.exception.NoMembershipException;
import com.itplace.userapi.user.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MembershipHistoryServiceImpl implements MembershipHistoryService {

    private final UserRepository userRepository;
    private final MembershipHistoryRepository historyRepository;

    @Override
    public PagedResponse<MembershipHistoryResponse> getUserHistory(
            Long userId,
            String keyword,
            LocalDate startDate,
            LocalDate endDate,
            Pageable pageable
    ) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(UserCode.USER_NOT_FOUND));

        String membershipId = user.getMembershipId();
        if (membershipId == null) {
            throw new NoMembershipException(UserCode.NO_MEMBERSHIP);
        }

        Page<MembershipHistory> page = historyRepository.findFiltered(
                membershipId,
                keyword,
                startDate != null ? startDate.atStartOfDay() : null,
                endDate != null ? endDate.atTime(LocalTime.MAX) : null,
                pageable
        );

        List<MembershipHistoryResponse> dtoList = page.stream()
                .map(mh -> {
                    Benefit b = mh.getBenefit();
                    Partner p = b.getPartner();
                    return MembershipHistoryResponse.builder()
                            .image(p.getImage())
                            .benefitName(b.getBenefitName())
                            .discountAmount(mh.getDiscountAmount())
                            .usedAt(mh.getUsedAt())
                            .build();
                })
                .toList();

        return new PagedResponse<>(
                dtoList,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements(),
                page.hasNext()
        );
    }

    @Override
    public MonthlyDiscountResponse getMonthlyDiscountSummary(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(UserCode.USER_NOT_FOUND));

        String membershipId = user.getMembershipId();
        if (membershipId == null) {
            throw new NoMembershipException(UserCode.NO_MEMBERSHIP);
        }

        LocalDate now = LocalDate.now();
        int year = now.getYear();
        int month = now.getMonthValue();

        Long totalDiscountAmount = historyRepository.sumDiscountAmountThisMonth(membershipId, year, month);

        return MonthlyDiscountResponse.builder()
                .userId(userId)
                .yearMonth(String.format("%04d-%02d", year, month))
                .totalDiscountAmount(totalDiscountAmount)
                .build();
    }
}

