package org.vitalii.vorobii.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.Map;

@RedisHash("statisticsResult")
@Data
@Builder
public class StatisticsResult {

    private static final Long EXPIRATION_MILLIS = 1000L * 600;

    @Id
    private Long createdAt;

    private Map<String, Double> commandTypeAverageExecution;

    @TimeToLive
    public Long getTimeToLive() {
        return EXPIRATION_MILLIS;
    }

}
