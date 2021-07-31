package org.vitalii.vorobii.service;

import java.util.Map;

public interface CommandExecutor {
    Object execute(Map<String, String> parameters);
}
