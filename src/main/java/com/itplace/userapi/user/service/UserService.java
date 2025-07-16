package com.itplace.userapi.user.service;

import com.itplace.userapi.user.dto.UserInfoDto;

public interface UserService {
    UserInfoDto getUserInfo(Long userId);
}