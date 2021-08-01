package org.vitalii.vorobii.service;

import org.vitalii.vorobii.entity.DoneCommand;
import org.vitalii.vorobii.entity.StatisticsResult;

public interface CommandStatisticService {
    void updateDataForAnalysis(DoneCommand doneCommand);

    void recalculate();

    StatisticsResult getLastStatistics();
}
