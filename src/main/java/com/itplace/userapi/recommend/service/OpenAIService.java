package com.itplace.userapi.recommend.service;

import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.UserFeature;
import java.util.List;

public interface OpenAIService {
    List<Candidate> vectorSearch(UserFeature uf, int topK);

    String rerankAndExplain(UserFeature uf, List<Candidate> cands, int topK);

}
