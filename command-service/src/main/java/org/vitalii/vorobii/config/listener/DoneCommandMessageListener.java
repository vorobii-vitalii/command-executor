package org.vitalii.vorobii.config.listener;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.config.service.DoneCommandMessageProcessor;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.listener.MessageListener;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoneCommandMessageListener implements MessageListener {
    private static final String ERROR_WHILE_PARSING_THE_DONE_COMMAND_FROM_MESSAGE =
            "Error while parsing the done command from message {}";

    private final ObjectMapper objectMapper;
    private final DoneCommandMessageProcessor doneCommandMessageProcessor;

    @Override
    public void handleMessage(String plainMessage) {
        var doneCommand = fetchCommandFromMessage(plainMessage);

        if (Objects.isNull(doneCommand)) {
            log.warn(ERROR_WHILE_PARSING_THE_DONE_COMMAND_FROM_MESSAGE, plainMessage);
            return;
        }
        doneCommandMessageProcessor.processDoneCommand(doneCommand);
    }

    private DoneCommand fetchCommandFromMessage(String message) {
        try {
            return objectMapper.readValue(message, DoneCommand.class);
        }
        catch (JsonProcessingException exception) {
            return null;
        }
    }

}
