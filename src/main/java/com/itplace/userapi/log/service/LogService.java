package com.itplace.userapi.log.service;

public interface LogService {
    void saveRequestLog(Long userId, String event, Long benefitId, String path, String param);

    void saveResponseLog(Long userId, String event, Long benefitId, Long partnerId, String path, String param);
}
