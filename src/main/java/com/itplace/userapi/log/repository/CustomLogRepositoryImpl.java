package com.itplace.userapi.log.repository;

import com.itplace.userapi.log.dto.LogScoreResult;
import com.itplace.userapi.log.dto.RankResult;
import java.time.Instant;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bson.Document;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.data.mongodb.core.aggregation.GroupOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SortOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class CustomLogRepositoryImpl implements CustomLogRepository {

    private final MongoTemplate mongoTemplate;

    @Override
    public List<LogScoreResult> aggregateUserLogScores(Long userId, int topK) {
        MatchOperation match = Aggregation.match(Criteria.where("userId").is(userId));

        AggregationOperation addScoreField = context -> new Document("$addFields",
                new Document("score",
                        new Document("$switch", new Document()
                                .append("branches", List.of(
                                        new Document("case", new Document("$eq", List.of("$event", "detail"))).append(
                                                "then", 3),
                                        new Document("case", new Document("$eq", List.of("$event", "search"))).append(
                                                "then", 2),
                                        new Document("case", new Document("$eq", List.of("$event", "click"))).append(
                                                "then", 1)
                                ))
                                .append("default", 0)
                        )
                )
        );

        GroupOperation group = Aggregation.group(
                Fields.fields("benefitId", "partnerName")
        ).sum("score").as("totalScore");

        ProjectionOperation project = Aggregation.project()
                .and("_id.benefitId").as("benefitId")
                .and("_id.partnerName").as("partnerName")
                .and("totalScore").as("totalScore");

        SortOperation sort = Aggregation.sort(Sort.by(Sort.Direction.DESC, "totalScore"));
        LimitOperation limit = Aggregation.limit(topK);

        Aggregation aggregation = Aggregation.newAggregation(
                match,
                addScoreField,
                group,
                project,
                sort,
                limit
        );

        AggregationResults<LogScoreResult> results =
                mongoTemplate.aggregate(aggregation, "logs", LogScoreResult.class);

        return results.getMappedResults();
    }

    @Override
    public List<RankResult> findTopSearchRank(Instant from, Instant to) {
        MatchOperation matchOperation = Aggregation.match(
                Criteria.where("event").is("search")
                        .and("loggingAt").gte(from).lt(to)
        );
        GroupOperation groupOperation = Aggregation.group("partnerId")
                .count().as("count");
        SortOperation sortOperation = Aggregation.sort(Direction.DESC, "count");
        LimitOperation limitOperation = Aggregation.limit(5);

        ProjectionOperation projectionOperation = Aggregation.project()
                .and("_id").as("partnerId")
                .and("count").as("count");

        Aggregation aggregation = Aggregation.newAggregation(
                matchOperation, groupOperation, sortOperation, limitOperation, projectionOperation);

        return mongoTemplate.aggregate(aggregation, "logs", RankResult.class)
                .getMappedResults();
    }
}


