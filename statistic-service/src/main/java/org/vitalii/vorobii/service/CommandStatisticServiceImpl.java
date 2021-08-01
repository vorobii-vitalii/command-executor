package org.vitalii.vorobii.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.entity.RedisCommand;
import org.vitalii.vorobii.entity.StatisticsResult;
import org.vitalii.vorobii.repository.CommandRepository;
import org.vitalii.vorobii.repository.StatisticsResultRepository;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommandStatisticServiceImpl implements CommandStatisticService {
    private static final String DATA_FOR_ANALYSIS_WAS_UPDATED = "Data for analysis was updated: {}";
    private static final String STATISTICS_WAS_UPDATED = "Statistics was updated";

    private final CommandRepository commandRepository;
    private final StatisticsResultRepository statisticsResultRepository;

    @Override
    public void updateDataForAnalysis(DoneCommand doneCommand) {
        RedisCommand redisCommand =
                RedisCommand.builder()
                    .commandType(doneCommand.getCommand().getCommandType())
                    .clientId(doneCommand.getCommand().getClientId())
                    .uuid(doneCommand.getCommand().getUuid())
                    .executionDurationMillis(retrieveExecutionDuration(doneCommand))
                .build();

        log.info(DATA_FOR_ANALYSIS_WAS_UPDATED, redisCommand);

        commandRepository.save(redisCommand);
    }

    @Override
    public void recalculate() {
        final Map<String, List<RedisCommand>> commandsGroupedByType = getCommandsGroupedByType();

        final Map<String, Double> averageExecutionsByType =
                calculateAverageExecutionsByType(commandsGroupedByType);

        StatisticsResult statisticsResult = StatisticsResult.builder()
                .commandTypeAverageExecution(averageExecutionsByType)
                .createdAt(Instant.now(Clock.systemUTC()).toEpochMilli())
                .build();

        log.info(STATISTICS_WAS_UPDATED);

        statisticsResultRepository.save(statisticsResult);
    }

    @Override
    public StatisticsResult getLastStatistics() {
        final List<StatisticsResult> statisticsResultList = new ArrayList<>();

        statisticsResultRepository.findAll().forEach(statisticsResult -> {
            if (statisticsResult != null) {
                statisticsResultList.add(statisticsResult);
            }
        });

        sortStatisticListByCreationDesc(statisticsResultList);

        return getFirstIfPresent(statisticsResultList);
    }

    private StatisticsResult getFirstIfPresent(List<StatisticsResult> statisticsResultList) {
        return statisticsResultList.isEmpty() ? null : statisticsResultList.get(0);
    }

    private void sortStatisticListByCreationDesc(List<StatisticsResult> statisticsResultList) {
        statisticsResultList.sort((res1, res2) ->
                res2.getCreatedAt().compareTo(res1.getCreatedAt()));
    }

    private Map<String, List<RedisCommand>> getCommandsGroupedByType() {
        final Map<String, List<RedisCommand>> commandsGroupedByType = new HashMap<>();

        commandRepository.findAll().forEach(redisCommand -> {
            if (redisCommand == null) {
                return;
            }
            String commandType = redisCommand.getCommandType();

            if (!commandsGroupedByType.containsKey(commandType)) {
                commandsGroupedByType.put(commandType, new ArrayList<>());
            }
            commandsGroupedByType.get(commandType).add(redisCommand);
        });
        return commandsGroupedByType;
    }

    private Map<String, Double> calculateAverageExecutionsByType(Map<String, List<RedisCommand>> commandsGroupedByType) {
        final Map<String, Double> averageExecutionsGroupedByType = new HashMap<>();

        commandsGroupedByType.forEach((type, commands) -> {

            Double averageExecution = commands.stream()
                    .collect(Collectors.averagingDouble(RedisCommand::getExecutionDurationMillis));

            averageExecutionsGroupedByType.put(type, averageExecution);
        });

        return averageExecutionsGroupedByType;
    }

    private Long retrieveExecutionDuration(DoneCommand doneCommand) {
        Instant requestedAt = doneCommand.getCommand().getRequestedAt();
        Instant finishedAt = doneCommand.getFinishedAt();

        return Duration.between(requestedAt, finishedAt).toMillis();
    }

}
