package org.vitalii.vorobii.listener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.service.CommandStatisticService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoneCommandMessageListenerTest {

    public static final String SOME_PLAIN_MESSAGE = "somePlainMessage";
    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private CommandStatisticService commandStatisticService;

    @InjectMocks
    private DoneCommandMessageListener commandMessageListener;

    @Mock
    private DoneCommand doneCommand;

    @Test
    void testHandleMessage_givenCorruptedMessage() {
        givenMessageSerializationFails();
        whenHandleMessage();
        thenMessageWasNotProcessed();
    }

    @Test
    void testHandleMessage_givenCorrectMessage() {
        givenMessageSerializationSucceeds();
        whenHandleMessage();
        thenMessageWasProcessed();
    }

    private void thenMessageWasProcessed() {
        verify(commandStatisticService, times(1))
                .updateDataForAnalysis(doneCommand);
    }

    @SneakyThrows
    private void givenMessageSerializationSucceeds() {
        when(objectMapper.readValue(anyString(), eq(DoneCommand.class)))
                .thenReturn(doneCommand);
    }

    private void thenMessageWasNotProcessed() {
        verify(commandStatisticService, never())
                .updateDataForAnalysis(any(DoneCommand.class));
    }

    private void whenHandleMessage() {
        commandMessageListener.handleMessage(SOME_PLAIN_MESSAGE);
    }

    @SneakyThrows
    private void givenMessageSerializationFails() {
        when(objectMapper.readValue(anyString(), eq(DoneCommand.class)))
                .thenThrow(JsonProcessingException.class);
    }

}
