package org.vitalii.vorobii.config.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.config.entity.CommandDTO;
import org.vitalii.vorobii.config.exception.CommandProcessingException;
import org.vitalii.vorobii.entity.Command;

import java.time.Clock;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class CommandServiceImpl implements CommandService {

    private static final String FAILURE_DURING_COMMAND_PROCESSING = "Failure during command processing";

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;
    private final Converter<CommandDTO, Command> publishedCommandConverter;

    @Override
    public void processCommand(CommandDTO command) {
        var publishedCommand = publishedCommandConverter.convert(command);

        Objects.requireNonNull(publishedCommand).setUuid(UUID.randomUUID());
        Objects.requireNonNull(publishedCommand).setRequestedAt(getCurrentTime());

        var serializedCommand = serializeCommand(publishedCommand);

        rabbitTemplate.convertAndSend(serializedCommand);
    }

    private Instant getCurrentTime() {
        return Instant.now(Clock.systemUTC());
    }

    private String serializeCommand(Command command) {
        try {
            return objectMapper.writeValueAsString(command);
        }
        catch (JsonProcessingException processingException) {
            log.error(FAILURE_DURING_COMMAND_PROCESSING);

            throw new CommandProcessingException();
        }
    }

}
