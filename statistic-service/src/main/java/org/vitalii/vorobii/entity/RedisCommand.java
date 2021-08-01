package org.vitalii.vorobii.entity;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;

import java.util.UUID;

@RedisHash("commands")
@Data
@Builder
public class RedisCommand {

    private static final Long EXPIRATION_MILLIS = 1000L * 120;

    @Id
    private UUID uuid;

    private String clientId;

    private String commandType;

    private Long executionDurationMillis;

    @TimeToLive
    public long getTimeToLive() {
        return EXPIRATION_MILLIS;
    }

}
