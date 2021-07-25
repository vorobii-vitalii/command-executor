package org.vitalii.vorobii.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
public class DoneCommand {
    private Instant finishedAt;
    private Object executionResult;
    private ExecutionStatus resultType;
    private Command command;
}
