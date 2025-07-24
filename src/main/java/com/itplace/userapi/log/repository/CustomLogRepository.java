package com.itplace.userapi.log.repository;

import com.itplace.userapi.log.dto.LogScoreResult;
import com.itplace.userapi.log.dto.RankResult;
import java.time.Instant;
import java.util.List;

public interface CustomLogRepository {
    List<LogScoreResult> aggregateUserLogScores(Long userId, int topK);

    List<RankResult> findTopSearchRank(Instant from, Instant to);
}
