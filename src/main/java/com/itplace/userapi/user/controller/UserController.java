package com.itplace.userapi.user.controller;

import com.itplace.userapi.common.ApiResponse;
import com.itplace.userapi.user.UserCode;
import com.itplace.userapi.user.dto.UserInfoDto;
import com.itplace.userapi.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<?>> getUser(@PathVariable Long userId) {
        UserInfoDto userInfoDto = userService.getUserInfo(userId);
        ApiResponse<?> body = ApiResponse.of(UserCode.USER_INFO_SUCCESS, userInfoDto);

        return ResponseEntity.status(body.getStatus()).body(body);
    }
}
