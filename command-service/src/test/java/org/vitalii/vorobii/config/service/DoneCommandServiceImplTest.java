package org.vitalii.vorobii.config.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vitalii.vorobii.config.dao.DoneCommandRepository;
import org.vitalii.vorobii.config.entity.MongoDoneCommand;
import org.vitalii.vorobii.entity.Command;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.entity.ExecutionStatus;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DoneCommandServiceImplTest {

    private static final String COMMAND_TYPE = "SIN";
    private static final String CLIENT_ID = "clientId";
    private static final Instant REQUESTED_AT = Instant.now();
    private static final ExecutionStatus EXECUTION_STATUS = ExecutionStatus.DONE;
    private static final Map<String, String> PARAMS = Map.of("number", "0.25");
    private static final Double EXECUTION_RESULT = 500D;

    @Mock
    private DoneCommandRepository doneCommandRepository;

    @InjectMocks
    private DoneCommandServiceImpl doneCommandService;

    @Mock
    private MongoDoneCommand mongoDoneCommand;

    private DoneCommand doneCommand;

    private List<MongoDoneCommand> doneCommands;

    @Test
    void testInsertCommand() {
        givenDoneCommand();
        whenInsertCommand();
        thenVerifyCorrectCommandWasInsertedInMongo();
    }

    @Test
    void testGetByClientId_givenNullResultList() {
        givenNullResultList();
        whenGetByClientId();
        thenExpectEmptyList();
    }

    @Test
    void testGetByClientId_givenNotEmptyResultList() {
        givenResultList();
        whenGetByClientId();
        thenVerifyList();
    }

    private void thenVerifyList() {
        verify(doneCommandRepository, times(1)).findByClientId(CLIENT_ID);

        Assertions.assertThat(doneCommands).containsExactly(mongoDoneCommand);
    }

    private void thenExpectEmptyList() {
        verify(doneCommandRepository, times(1)).findByClientId(CLIENT_ID);

        assertEquals(Collections.emptyList(), doneCommands);
    }

    private void whenGetByClientId() {
        this.doneCommands = doneCommandService.getByClientId(CLIENT_ID);
    }

    private void givenNullResultList() {
        when(doneCommandRepository.findByClientId(anyString())).thenReturn(null);
    }

    private void givenResultList() {
        when(doneCommandRepository.findByClientId(anyString())).thenReturn(List.of(mongoDoneCommand));
    }

    private void thenVerifyCorrectCommandWasInsertedInMongo() {

        var mongoDoneCommand =
                MongoDoneCommand.builder()
                    .executionResult(EXECUTION_RESULT)
                    .clientId(CLIENT_ID)
                    .requestedAt(REQUESTED_AT)
                    .executionStatus(EXECUTION_STATUS)
                    .commandType(COMMAND_TYPE)
                    .params(PARAMS)
                .build();

        verify(doneCommandRepository, times(1))
                .save(mongoDoneCommand);
    }

    private void whenInsertCommand() {
        doneCommandService.insertCommand(doneCommand);
    }

    private void givenDoneCommand() {

        var command =
            Command.builder()
                .commandType(COMMAND_TYPE)
                .clientId(CLIENT_ID)
                .requestedAt(REQUESTED_AT)
                .params(PARAMS)
            .build();

        this.doneCommand =
            DoneCommand.builder()
                .command(command)
                .resultType(EXECUTION_STATUS)
                .executionResult(EXECUTION_RESULT)
            .build();
    }

}
