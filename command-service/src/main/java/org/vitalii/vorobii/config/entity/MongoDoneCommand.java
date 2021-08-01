package org.vitalii.vorobii.config.entity;

import lombok.Builder;
import lombok.Data;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.vitalii.vorobii.entity.ExecutionStatus;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@Document
@Data
@Builder
public class MongoDoneCommand {
    @Id
    private ObjectId id;
    private String clientId;
    private Object executionResult;
    private ExecutionStatus executionStatus;
    private String commandType;
    private Map<String, String> params = new HashMap<>();
    private Instant requestedAt;
}
