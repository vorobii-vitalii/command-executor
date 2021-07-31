package org.vitalii.vorobii.service;

import org.springframework.stereotype.Service;
import org.vitalii.vorobii.entity.CommandType;

@Service
public class CommandExecutorFactoryImpl implements CommandExecutorFactory {
    @Override
    public CommandExecutor createCommandExecutor(CommandType commandType) {
        return switch (commandType) {
            case COS -> new CosCommandExecutor();
            case SIN -> new SinCommandExecutor();
            default -> null;
        };
    }
}
