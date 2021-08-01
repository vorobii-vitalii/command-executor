package org.vitalii.vorobii.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.service.CommandStatisticService;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoneCommandMessageListener implements MessageListener {
    public static final String ERROR_WHILE_PARSING_COMMAND_FROM_MESSAGE = "Error while parsing command from message: {}";
    private final ObjectMapper objectMapper;
    private final CommandStatisticService commandStatisticService;

    @Override
    public void handleMessage(String plainMessage) {
        DoneCommand doneCommand = fetchCommandFromMessage(plainMessage);

        if (doneCommand == null) {
            log.error(ERROR_WHILE_PARSING_COMMAND_FROM_MESSAGE, plainMessage);
            return;
        }
        commandStatisticService.updateDataForAnalysis(doneCommand);
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
