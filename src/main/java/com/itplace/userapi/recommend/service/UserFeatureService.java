package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.dto.UserFeature;

public interface UserFeatureService {
    UserFeature loadUserFeature(Long userId);
}
