package org.vitalii.vorobii.config.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.entity.DoneCommand;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoneCommandMessageProcessorImpl implements DoneCommandMessageProcessor {

    private static final String RECEIVED_DONE_COMMAND_FOR_PROCESSING =
            "Received done command {} for processing.";

    private final DoneCommandService doneCommandService;

    @Override
    public void processDoneCommand(DoneCommand doneCommand) {

        log.info(RECEIVED_DONE_COMMAND_FOR_PROCESSING, doneCommand);

        doneCommandService.insertCommand(doneCommand);
    }

}
