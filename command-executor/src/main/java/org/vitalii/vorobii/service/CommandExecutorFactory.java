package org.vitalii.vorobii.service;

import org.vitalii.vorobii.entity.CommandType;

public interface CommandExecutorFactory {
    CommandExecutor createCommandExecutor(CommandType commandType);
}
