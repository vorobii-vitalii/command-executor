package org.vitalii.vorobii.config.service;

import org.vitalii.vorobii.config.entity.MongoDoneCommand;
import org.vitalii.vorobii.entity.DoneCommand;

import java.util.List;

public interface DoneCommandService {
    void insertCommand(DoneCommand doneCommand);

    List<MongoDoneCommand> getByClientId(String clientId);
}
