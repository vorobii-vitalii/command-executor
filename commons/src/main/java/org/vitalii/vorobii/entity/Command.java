package org.vitalii.vorobii.entity;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@Builder
public class Command {
    private UUID uuid;
    private String clientId;
    private String commandType;
    private Map<String, String> params = new HashMap<>();
    private Instant requestedAt;
}
