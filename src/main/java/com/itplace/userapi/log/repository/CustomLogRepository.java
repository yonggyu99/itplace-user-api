package com.itplace.userapi.log.repository;

import com.itplace.userapi.log.dto.LogScoreResult;
import java.util.List;

public interface CustomLogRepository {
    List<LogScoreResult> aggregateUserLogScores(Long userId, int topK);
}
