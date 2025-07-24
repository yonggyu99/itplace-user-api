package com.itplace.userapi.log;

import com.itplace.userapi.log.dto.LogScoreResult;
import com.itplace.userapi.log.service.LogService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/log-analysis")
@RequiredArgsConstructor
public class LogAnalysisController {

    private final LogService logService;

    @GetMapping("/{userId}")
    public ResponseEntity<List<LogScoreResult>> getLogScoreByUser(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int topK
    ) {
        List<LogScoreResult> scores = logService.getUserLogScores(userId, topK);
        return ResponseEntity.ok(scores);
    }
}

