package com.itplace.userapi.recommend.strategy;

import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.UserFeature;
import com.itplace.userapi.recommend.service.OpenAIService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component("openAiRetrieval")
@RequiredArgsConstructor
public class OpenAiRetrievalStrategy implements RetrievalStrategy {
    private final OpenAIService aiService;

    @Override
    public List<Candidate> retrieve(UserFeature uf, int candidateSize) {
        return aiService.vectorSearch(uf, candidateSize);
    }
}
