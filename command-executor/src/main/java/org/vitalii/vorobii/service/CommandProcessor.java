package org.vitalii.vorobii.service;

import org.vitalii.vorobii.entity.Command;

public interface CommandProcessor {
    void processCommand(Command command);
}
