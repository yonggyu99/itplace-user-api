package com.itplace.userapi.user.service;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.EmailVerificationException;
import com.itplace.userapi.security.exception.SmsVerificationException;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.security.verification.OtpUtil;
import com.itplace.userapi.security.verification.email.dto.EmailConfirmRequest;
import com.itplace.userapi.user.dto.UserInfoDto;
import com.itplace.userapi.user.dto.request.FindEmailConfirmRequest;
import com.itplace.userapi.user.dto.response.FindEmailResponse;
import com.itplace.userapi.user.entity.Membership;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.MembershipRepository;
import com.itplace.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;
    private final OtpUtil otpUtil;

    @Override
    public UserInfoDto getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));

        Membership membership = null;
        if (user.getMembershipId() != null) {
            membership = membershipRepository.findById(user.getMembershipId())
                    .orElse(null);
        }

        return UserInfoDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .gender(user.getGender())
                .birthday(user.getBirthday())
                .membershipId(membership != null ? membership.getMembershipId() : null)
                .grade(membership != null ? membership.getGrade() : null)
                .build();
    }

    @Override
    public FindEmailResponse findEmailConfirm(FindEmailConfirmRequest request) {
        if (otpUtil.validateSmsOtp(request.getPhoneNumber(), request.getVerificationCode())) {
            log.info("SMS 인증 성공");
            User user = userRepository.findByPhoneNumberAndName(request.getPhoneNumber(), request.getName())
                    .orElseThrow(() -> new UserNotFoundException(SecurityCode.USER_NOT_FOUND));
            return FindEmailResponse.builder()
                    .email(user.getEmail())
                    .build();
        } else {
            log.info("SMS 인증 실패");
            throw new SmsVerificationException(SecurityCode.SMS_VERIFICATION_FAILURE);
        }
    }

    @Override
    public void findPasswordConfirm(EmailConfirmRequest request) {
        if (otpUtil.validateEmailOtp(request.getEmail(), request.getVerificationCode())) {
            log.info("Email 인증 성공");
        } else {
            log.info("Email 인증 실패");
            throw new EmailVerificationException(SecurityCode.EMAIL_VERIFICATION_FAILURE);
        }
    }
}