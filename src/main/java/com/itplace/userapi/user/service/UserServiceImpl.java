package com.itplace.userapi.user.service;

import com.itplace.userapi.common.exception.DuplicateEmailException;
import com.itplace.userapi.common.exception.DuplicatePhoneNumberException;
import com.itplace.userapi.common.exception.PasswordMismatchException;
import com.itplace.userapi.security.auth.dto.request.SignUpRequest;
import com.itplace.userapi.user.entity.Role;
import com.itplace.userapi.user.entity.User;
import com.itplace.userapi.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void signUp(SignUpRequest request) {
        userRepository.findByEmailOrPhoneNumber(request.getEmail(), request.getPhoneNumber())
                .ifPresent(user -> {
                    if (user.getEmail().equals(request.getEmail())) {
                        throw new DuplicateEmailException("이미 사용 중인 이메일입니다.");
                    } else if (user.getPhoneNumber().equals(request.getPhoneNumber())) {
                        throw new DuplicatePhoneNumberException("이미 사용 중인 전화번호입니다.");
                    }
                });

        if (!request.getPassword().equals(request.getPasswordConfirm())) {
            throw new PasswordMismatchException("비밀번호를 다시 확인해주세요");
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .name(request.getName())
                .gender(request.getGender())
                .phoneNumber(request.getPhoneNumber())
                .birthday(request.getBirthday())
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }
}