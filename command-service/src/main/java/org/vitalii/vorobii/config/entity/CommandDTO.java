package org.vitalii.vorobii.config.entity;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
public class CommandDTO {

    @NotNull
    private String clientId;

    @NotNull
    private String commandType;

    private Map<String, String> params = new HashMap<>();

}
