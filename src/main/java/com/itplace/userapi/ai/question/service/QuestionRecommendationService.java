package com.itplace.userapi.ai.question.service;

import com.itplace.userapi.map.dto.StoreDetailDto;
import com.itplace.userapi.ai.llm.dto.RecommendationResponse;
import java.util.List;

public interface QuestionRecommendationService {
    List<StoreDetailDto> recommendByQuestion(String question, double lat, double lng) throws Exception;
}
