package org.vitalii.vorobii.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vitalii.vorobii.entity.Command;
import org.vitalii.vorobii.service.CommandProcessor;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandMessageListenerImplTest {

    public static final String CLIENT_ID = "clientId";
    public static final String COMMAND_TYPE = "SIN";
    public static final UUID RANDOM_UUID = UUID.randomUUID();
    public static final String PLAIN_MESSAGE = "somePlainMessage";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CommandProcessor commandProcessor;

    @InjectMocks
    private CommandMessageListenerImpl commandMessageListener;

    private final Command command = getCommand();

    @BeforeEach
    void init() {
        commandMessageListener.setCommandProcessor(commandProcessor);
    }

    @Test
    void testHandleMessage_givenMessageIsCorrupted() {
        givenMessageParsingFails();
        whenHandleMessage();
        verifyCommandProcessingIsSkipped();
    }

    @Test
    void testHandleMessage_givenMessageIsLegitimate() {
        givenMessageParsingSucceeds();
        whenHandleMessage();
        verifyCommandProcessingIsDoneOnce();
    }

    @SneakyThrows
    private void givenMessageParsingSucceeds() {
        when(objectMapper.readValue(anyString(), any(Class.class)))
                .thenReturn(command);
    }

    @SneakyThrows
    private void givenMessageParsingFails() {
        when(objectMapper.readValue(anyString(), any(Class.class)))
                .thenThrow(JsonProcessingException.class);
    }

    private void whenHandleMessage() {
        commandMessageListener.handleMessage(PLAIN_MESSAGE);
    }

    private void verifyCommandProcessingIsDoneOnce() {
        verify(commandProcessor, times(1))
                .processCommand(command);
    }

    private void verifyCommandProcessingIsSkipped() {
        verifyNoInteractions(commandProcessor);
    }

    private Command getCommand() {
        return Command
                .builder()
                .clientId(CLIENT_ID)
                .commandType(COMMAND_TYPE)
                .uuid(RANDOM_UUID)
                .build();
    }

}
