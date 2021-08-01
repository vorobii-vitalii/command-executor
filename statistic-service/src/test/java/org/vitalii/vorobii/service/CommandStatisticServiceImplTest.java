package org.vitalii.vorobii.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.vitalii.vorobii.entity.Command;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.entity.RedisCommand;
import org.vitalii.vorobii.entity.StatisticsResult;
import org.vitalii.vorobii.repository.CommandRepository;
import org.vitalii.vorobii.repository.StatisticsResultRepository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommandStatisticServiceImplTest {

    public static final String COMMAND_TYPE = "SIN";
    public static final String CLIENT_ID = "2";
    public static final UUID RANDOM_UUID = UUID.randomUUID();
    public static final long DELAY = 50L;
    public static final String COMMAND_TYPE_1 = "SIN";
    public static final long DELAY_1 = 500L;
    public static final long DELAY_2 = 100L;
    public static final String COMMAND_TYPE_2 = "COS";

    @Mock
    private CommandRepository commandRepository;

    @Mock
    private StatisticsResultRepository statisticsResultRepository;

    @InjectMocks
    private CommandStatisticServiceImpl commandStatisticService;

    private DoneCommand doneCommand;
    private StatisticsResult lastStatistic;

    @Test
    void testUpdateDataForAnalysis() {
        givenDoneCommand();
        whenUpdateDataForAnalysis();
        thenVerifyCorrectCommandWasInserted();
    }

    @Test
    void testRecalculate() {
        givenCommandsFromRedis();
        whenRecalculate();
        thenVerifyInsertedStatisticResult();
    }

    @Test
    void testGetLastStatisticGivenNoResult() {
        givenStatisticResultListEmpty();
        whenGetLastStatistic();
        thenLastStatisticIsNull();
    }

    @Test
    void testGetLastStatisticGivenNotEmptyResult() {
        givenStatisticResultListNotEmpty();
        whenGetLastStatistic();
        thenVerifyLastStatistic();
    }

    private void thenVerifyLastStatistic() {
        Long actualCreatedAt = lastStatistic.getCreatedAt();

        assertEquals(Long.MAX_VALUE, actualCreatedAt);
    }

    private void givenStatisticResultListNotEmpty() {
        StatisticsResult statisticsResult1 = StatisticsResult.builder()
                .createdAt(Long.MIN_VALUE)
                .build();

        StatisticsResult statisticsResult2 = StatisticsResult.builder()
                .createdAt(Long.MAX_VALUE)
                .build();

        when(statisticsResultRepository.findAll())
                .thenReturn(List.of(statisticsResult1, statisticsResult2));
    }

    private void thenLastStatisticIsNull() {
        assertNull(this.lastStatistic);
    }

    private void whenGetLastStatistic() {
        this.lastStatistic = commandStatisticService.getLastStatistics();
    }

    private void givenStatisticResultListEmpty() {
        when(statisticsResultRepository.findAll()).thenReturn(Collections.emptyList());
    }

    private void thenVerifyInsertedStatisticResult() {

        Map<String, Double> expectedAverageExecutions =
                Map.of(COMMAND_TYPE_1, (DELAY_1 + DELAY_2) / 2.0, COMMAND_TYPE_2, (double) DELAY_1);

        verify(statisticsResultRepository, times(1))
                .save(argThat(statisticsResult ->
                        expectedAverageExecutions.equals(statisticsResult.getCommandTypeAverageExecution())));
    }

    private void whenRecalculate() {
        commandStatisticService.recalculate();
    }

    private void givenCommandsFromRedis() {

        RedisCommand redisCommand1 =
                RedisCommand.builder()
                    .commandType(COMMAND_TYPE_1)
                    .executionDurationMillis(DELAY_1)
                .build();

        RedisCommand redisCommand2 =
                RedisCommand.builder()
                        .commandType(COMMAND_TYPE_1)
                        .executionDurationMillis(DELAY_2)
                        .build();

        RedisCommand redisCommand3 =
                RedisCommand.builder()
                        .commandType(COMMAND_TYPE_2)
                        .executionDurationMillis(DELAY_1)
                        .build();

        List<RedisCommand> redisCommands =
                List.of(redisCommand1, redisCommand2, redisCommand3);

        when(commandRepository.findAll()).thenReturn(redisCommands);
    }

    private void thenVerifyCorrectCommandWasInserted() {
        RedisCommand expectedRedisCommand =
                RedisCommand.builder()
                        .executionDurationMillis(DELAY)
                        .commandType(COMMAND_TYPE)
                        .clientId(CLIENT_ID)
                        .uuid(RANDOM_UUID)
                        .build();

        verify(commandRepository, times(1))
                .save(expectedRedisCommand);
    }

    private void whenUpdateDataForAnalysis() {
        commandStatisticService.updateDataForAnalysis(doneCommand);
    }

    private void givenDoneCommand() {
        this.doneCommand =
                DoneCommand.builder()
                    .command(
                        Command.builder()
                            .commandType(COMMAND_TYPE)
                            .clientId(CLIENT_ID)
                            .uuid(RANDOM_UUID)
                            .requestedAt(Instant.MIN)
                        .build())
                    .finishedAt(Instant.MIN.plusMillis(DELAY))
                .build();
    }

}
