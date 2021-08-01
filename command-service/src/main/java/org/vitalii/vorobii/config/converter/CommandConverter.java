package org.vitalii.vorobii.config.converter;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.vitalii.vorobii.config.entity.CommandDTO;
import org.vitalii.vorobii.entity.Command;

@Component
public class CommandConverter implements Converter<CommandDTO, Command> {
    @Override
    public Command convert(CommandDTO command) {
        return Command.builder()
                .commandType(command.getCommandType())
                .clientId(command.getClientId())
                .params(command.getParams())
                .build();
    }
}
