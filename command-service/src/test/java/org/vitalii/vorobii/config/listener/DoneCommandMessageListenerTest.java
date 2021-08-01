package org.vitalii.vorobii.config.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vitalii.vorobii.config.service.DoneCommandMessageProcessor;
import org.vitalii.vorobii.entity.DoneCommand;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

@ExtendWith(MockitoExtension.class)
class DoneCommandMessageListenerTest {

    public static final String MESSAGE = "someMessage";

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private DoneCommandMessageProcessor doneCommandMessageProcessor;

    @InjectMocks
    private DoneCommandMessageListener doneCommandMessageListener;

    @Mock
    private DoneCommand doneCommand;

    @Test
    void testHandleMessage_givenSerializationFails() {
        givenSerializationFails();
        whenHandleMessage();
        thenMessageProcessingCancelled();
    }

    @Test
    void testHandleMessage_givenSerializationSucceeds() {
        givenSerializationSucceeds();
        whenHandleMessage();
        thenMessageIsProcessed();
    }

    private void thenMessageIsProcessed() {
        Mockito.verify(doneCommandMessageProcessor, Mockito.times(1))
                .processDoneCommand(doneCommand);
    }

    private void thenMessageProcessingCancelled() {
        Mockito.verify(doneCommandMessageProcessor, Mockito.never())
                .processDoneCommand(any(DoneCommand.class));
    }

    private void whenHandleMessage() {
        doneCommandMessageListener.handleMessage(MESSAGE);
    }

    @SneakyThrows
    private void givenSerializationSucceeds() {
        Mockito.when(objectMapper.readValue(anyString(), eq(DoneCommand.class)))
                .thenReturn(doneCommand);
    }

    @SneakyThrows
    private void givenSerializationFails() {
        Mockito.when(objectMapper.readValue(anyString(), eq(DoneCommand.class)))
                .thenThrow(JsonProcessingException.class);
    }

}