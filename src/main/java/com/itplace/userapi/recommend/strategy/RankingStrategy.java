package com.itplace.userapi.recommend.strategy;

import com.itplace.userapi.recommend.dto.Candidate;
import com.itplace.userapi.recommend.dto.UserFeature;
import java.util.List;

public interface RankingStrategy {
    String rank(UserFeature uf, List<Candidate> candidates, int topK);
}
