package com.itplace.userapi.rag.service;

import com.itplace.userapi.recommend.dto.Candidate;
import java.util.List;

public interface BenefitSearchService {
    List<Candidate> queryVector(List<Float> userEmbedding, int topK);
}
