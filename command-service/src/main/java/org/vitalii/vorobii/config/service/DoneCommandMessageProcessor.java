package org.vitalii.vorobii.config.service;

import org.vitalii.vorobii.entity.DoneCommand;

public interface DoneCommandMessageProcessor {
    void processDoneCommand(DoneCommand doneCommand);
}
