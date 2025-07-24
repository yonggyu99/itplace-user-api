package com.itplace.userapi.log.service;

import com.itplace.userapi.benefit.entity.Benefit;
import com.itplace.userapi.benefit.repository.BenefitRepository;
import com.itplace.userapi.log.dto.LogScoreResult;
import com.itplace.userapi.log.entity.LogDocument;
import com.itplace.userapi.log.repository.LogRepository;
import jakarta.transaction.Transactional;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class LogServiceImpl implements LogService {

    private final LogRepository logRepository;
    private final BenefitRepository benefitRepository;

    // 클릭
    @Override
    public void saveRequestLog(Long userId, String event, Long benefitId, String path, String param) {
        Benefit benefit = benefitRepository.findById(benefitId).orElse(null);
        if (benefit != null) {
            Long partnerId = benefit.getPartner().getPartnerId();
            String partnerName = benefit.getPartner().getPartnerName();

            log.info("REQUEST: {}, path={}, event={}, benefitId={}, partnerId={}",
                    userId, path, event, benefitId, partnerId);

            LogDocument logDocument = LogDocument.builder()
                    .userId(userId)
                    .event(event)
                    .benefitId(benefitId)
                    .benefitName(benefit.getBenefitName())
                    .partnerId(partnerId)
                    .partnerName(partnerName)
                    .path(path)
                    .param(param)
                    .loggingAt(Instant.now())
                    .build();
            logRepository.save(logDocument);
        }
    }

    // 검색, 상세
    @Override
    public void saveResponseLog(Long userId, String event, Long benefitId, Long partnerId, String path, String param) {
        Benefit benefit = benefitRepository.findById(benefitId).orElse(null);
        if (benefit != null) {
            String partnerName = benefit.getPartner().getPartnerName();

            log.info("RESPONSE: {}, path={}, event={}, benefitId={}, partnerId={}",
                    userId, path, event, benefitId, partnerId);

            LogDocument logDocument = LogDocument.builder()
                    .userId(userId)
                    .event(event)
                    .benefitId(benefitId)
                    .benefitName(benefit.getBenefitName())
                    .partnerId(partnerId)
                    .partnerName(partnerName)
                    .path(path)
                    .param(param)
                    .loggingAt(Instant.now())
                    .build();
            logRepository.save(logDocument);
        }
    }

    @Override
    public List<LogScoreResult> getUserLogScores(Long userId, int topK) {
        return logRepository.aggregateUserLogScores(userId, topK);
    }
}
