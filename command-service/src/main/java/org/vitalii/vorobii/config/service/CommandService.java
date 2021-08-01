package org.vitalii.vorobii.config.service;

import org.vitalii.vorobii.config.entity.CommandDTO;

public interface CommandService {
    void processCommand(CommandDTO command);
}
