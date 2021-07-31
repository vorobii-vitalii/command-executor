package org.vitalii.vorobii.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.entity.Command;
import org.vitalii.vorobii.service.CommandProcessor;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandMessageListenerImpl implements MessageListener {
    private static final String ERROR_WHILE_PARSING_COMMAND = "Error while parsing command {}";
    private final ObjectMapper objectMapper;

    private CommandProcessor commandProcessor;

    @Override
    public void handleMessage(String plainMessage) {
        var command = extractCommand(plainMessage);

        if (command == null) {
            log.error(ERROR_WHILE_PARSING_COMMAND, plainMessage);
            return;
        }
        commandProcessor.processCommand(command);
    }

    private Command extractCommand(String plainMessage) {
        try {
            return objectMapper.readValue(plainMessage, Command.class);
        } catch (JsonProcessingException processingException) {
            return null;
        }
    }

    @Autowired
    public void setCommandProcessor(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

}
