package org.vitalii.vorobii.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.vitalii.vorobii.entity.Command;
import org.vitalii.vorobii.entity.CommandType;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.entity.ExecutionStatus;
import org.vitalii.vorobii.exception.ExecutionFailedException;

import java.util.HashMap;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CommandProcessorImplTest {

    public static final String UNSUPPORTED_COMMAND_TYPE = "unsupportedCommandType";
    public static final String SUPPORTED_COMMAND_TYPE = "SIN";
    public static final HashMap<String, String> PARAMS = new HashMap<>();
    public static final String RESULT = "someResult";
    public static final String JSON_STRING = "{}";

    public static final ArgumentMatcher<DoneCommand> FAILED_COMMAND_MATCHER =
            doneCommand -> ExecutionStatus.FAILED.equals(doneCommand.getResultType());

    public static final ArgumentMatcher<DoneCommand> DONE_COMMAND_MATCHER =
            doneCommand -> ExecutionStatus.DONE.equals(doneCommand.getResultType());

    public static final ArgumentMatcher<DoneCommand> EMPTY_RESULT_MATCHER =
            doneCommand -> doneCommand.getExecutionResult() == null;

    public static final ArgumentMatcher<DoneCommand> NOT_EMPTY_RESULT_MATCHER =
            doneCommand -> RESULT.equals(doneCommand.getExecutionResult());

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CommandExecutorFactory commandExecutorFactory;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private CommandProcessorImpl commandProcessor;

    @Mock
    private CommandExecutor commandExecutor;

    private Command command;

    @BeforeEach
    void init() {
        commandProcessor.setRabbitTemplate(rabbitTemplate);
    }

    @Test
    void testProcessCommand_givenCommandWithUnsupportedType() {
        givenCommandWithUnsupportedType();
        givenSerializationSucceeds();
        whenProcessCommand();
        verifyCorrectMessageWasSerialized(DONE_COMMAND_MATCHER, EMPTY_RESULT_MATCHER);
        verifyMessageWasSent();
    }

    @Test
    void testProcessCommand_givenCommandExecutionFails() {
        givenCommandWithSupportedType();
        givenCommandExecutorIsFound();
        givenCommandExecutionFails();
        givenSerializationSucceeds();
        whenProcessCommand();
        verifyCorrectMessageWasSerialized(FAILED_COMMAND_MATCHER);
        verifyMessageWasSent();
    }

    @Test
    void testProcessCommand_givenSerializationFails() {
        givenCommandWithSupportedType();
        givenSerializationFails();
        whenProcessCommand();
        verifyMessageWasNotSent();
    }

    @SneakyThrows
    private void verifyCorrectMessageWasSerialized(ArgumentMatcher<DoneCommand> ... matchers) {
        verify(objectMapper, times(1))
                .writeValueAsString(argThat(complexMatcher(matchers)));
    }

    private void givenCommandWithUnsupportedType() {
        this.command =
                Command.builder()
                    .commandType(UNSUPPORTED_COMMAND_TYPE)
                    .params(PARAMS)
                .build();
    }

    private void givenCommandWithSupportedType() {
        this.command =
                Command.builder()
                        .commandType(SUPPORTED_COMMAND_TYPE)
                        .params(PARAMS)
                        .build();
    }

    private void givenCommandExecutorIsFound() {
        when(commandExecutorFactory.createCommandExecutor(any(CommandType.class)))
                .thenReturn(commandExecutor);
    }

    private void givenCommandExecutionFails() {
        when(commandExecutor.execute(anyMap()))
                .thenThrow(ExecutionFailedException.class);
    }

    @SneakyThrows
    private void givenSerializationSucceeds() {
        when(objectMapper.writeValueAsString(any(DoneCommand.class)))
                .thenReturn(JSON_STRING);
    }

    @SneakyThrows
    private void givenSerializationFails() {
        when(objectMapper.writeValueAsString(any(DoneCommand.class)))
                .thenThrow(JsonProcessingException.class);
    }

    private void whenProcessCommand() {
        commandProcessor.processCommand(command);
    }

    private void verifyMessageWasSent() {
        verify(rabbitTemplate, times(1))
                .convertAndSend(JSON_STRING);
    }

    private void verifyMessageWasNotSent() {
        verify(rabbitTemplate, never())
                .convertAndSend(any());
    }

    private <T> ArgumentMatcher<T> complexMatcher(ArgumentMatcher<T>... matchers) {
        return arg -> Stream.of(matchers)
                .allMatch(matcher -> matcher.matches(arg));
    }

}
