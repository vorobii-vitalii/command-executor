package org.vitalii.vorobii.config.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.core.convert.converter.Converter;
import org.vitalii.vorobii.config.entity.CommandDTO;
import org.vitalii.vorobii.config.exception.CommandProcessingException;
import org.vitalii.vorobii.entity.Command;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandServiceImplTest {

    private static final String EXPECTED_JSON = "{'requesterName': 'John' }";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @Mock
    private Converter<CommandDTO, Command> publishedCommandConverter;

    @InjectMocks
    private CommandServiceImpl commandService;

    @Mock
    private Command command;

    private CommandDTO commandDTO;

    @Test
    void testProcessCommand_givenSerializationFails() {
        givenCommand();
        givenConversionWorksFine();
        givenSerializationFails();
        assertThrows(CommandProcessingException.class, this::whenProcessCommand);
        verifyMessageWasNotSent();
    }

    @Test
    void testProcessCommand_givenSerializationSucceeds() {
        givenCommand();
        givenConversionWorksFine();
        givenSerializationWorksFine();
        whenProcessCommand();
        verifyMessageWasSent();
    }

    private void givenConversionWorksFine() {
        when(publishedCommandConverter.convert(any(CommandDTO.class)))
                .thenReturn(command);
    }

    private void givenCommand() {
        commandDTO = new CommandDTO();
    }

    @SneakyThrows
    private void givenSerializationFails() {
        when(objectMapper.writeValueAsString(any()))
                .thenThrow(JsonProcessingException.class);
    }

    @SneakyThrows
    private void givenSerializationWorksFine() {
        when(objectMapper.writeValueAsString(any()))
                .thenReturn(EXPECTED_JSON);
    }

    private void whenProcessCommand() {
        commandService.processCommand(commandDTO);
    }

    private void verifyMessageWasSent() {
        verify(rabbitTemplate, times(1))
                .convertAndSend(EXPECTED_JSON);
    }

    private void verifyMessageWasNotSent() {
        verifyNoInteractions(rabbitTemplate);
    }

}
