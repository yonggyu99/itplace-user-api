package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.Recommendation;
import java.util.List;

public interface OpenAIService {
    List<Candidate> vectorSearch(UserFeature uf, int topK);

    List<Recommendation> rerankAndExplain(UserFeature uf, List<Candidate> cands, int topK);

}
