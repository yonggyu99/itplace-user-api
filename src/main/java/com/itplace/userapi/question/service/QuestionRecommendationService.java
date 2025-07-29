package com.itplace.userapi.question.service;

import com.itplace.userapi.map.dto.StoreDetailDto;
import java.util.List;

public interface QuestionRecommendationService {
    List<StoreDetailDto> recommendByQuestion(String question, double lat, double lng) throws Exception;

}
