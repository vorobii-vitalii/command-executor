package org.vitalii.vorobii.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.entity.Command;
import org.vitalii.vorobii.entity.CommandType;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.entity.ExecutionStatus;
import org.vitalii.vorobii.exception.ExecutionFailedException;

import java.time.Clock;
import java.time.Instant;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommandProcessorImpl implements CommandProcessor {
    public static final String ERROR_OCCURRED_DURING_COMMAND_SERIALIZATION = "Error occurred during command serialization";

    private final CommandExecutorFactory commandExecutorFactory;
    private final ObjectMapper objectMapper;
    private RabbitTemplate rabbitTemplate;

    @Override
    public void processCommand(Command command) {
        var commandType = CommandType.fromName(command.getCommandType());
        var commandParams = command.getParams();

        Object executionResult = null;
        var executionStatus = ExecutionStatus.DONE;

        try {
            executionResult =
                Optional.ofNullable(commandType)
                        .map(commandExecutorFactory::createCommandExecutor)
                        .map(commandExecutor -> commandExecutor.execute(commandParams))
                        .orElse(null);

        } catch (ExecutionFailedException e) {
            executionStatus = ExecutionStatus.FAILED;
        }

        var doneCommand =
                DoneCommand.builder()
                    .command(command)
                    .finishedAt(getCurrentTime())
                    .executionResult(executionResult)
                    .resultType(executionStatus)
                .build();

        try {
            var serializedDoneCommand = serializeDoneCommand(doneCommand);

            rabbitTemplate.convertAndSend(serializedDoneCommand);
        }
        catch (JsonProcessingException e) {
            log.error(ERROR_OCCURRED_DURING_COMMAND_SERIALIZATION);
        }

    }

    private Instant getCurrentTime() {
        return Instant.now(Clock.systemUTC());
    }

    private String serializeDoneCommand(DoneCommand command) throws JsonProcessingException {
        return objectMapper.writeValueAsString(command);
    }

    @Autowired
    public void setRabbitTemplate(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

}
