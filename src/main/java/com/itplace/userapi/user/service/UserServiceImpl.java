package com.itplace.userapi.user.service;

import com.itplace.userapi.security.SecurityCode;
import com.itplace.userapi.security.exception.UserNotFoundException;
import com.itplace.userapi.user.dto.UserInfoDto;
import com.itplace.userapi.user.entity.Membership;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.MembershipRepository;
import com.itplace.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final MembershipRepository membershipRepository;

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
}