package com.itplace.userapi.recommend.strategy;

import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.Recommendation;
import java.util.List;

public interface RankingStrategy {
    List<Recommendation> rank(UserFeature uf, List<Candidate> candidates, int topK);
}
