package com.itplace.userapi.recommend.strategy;

import com.itplace.userapi.recommend.domain.UserFeature;
import com.itplace.userapi.recommend.dto.Candidate;
import java.util.List;

public interface RetrievalStrategy {
    List<Candidate> retrieve(UserFeature uf, int candidateSize);
}
