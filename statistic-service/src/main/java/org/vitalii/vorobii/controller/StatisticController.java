package org.vitalii.vorobii.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vitalii.vorobii.entity.StatisticsResult;
import org.vitalii.vorobii.service.CommandStatisticService;

@RestController
@RequiredArgsConstructor
@RequestMapping(StatisticController.STATISTICS_ENDPOINT)
public class StatisticController {
    public static final String STATISTICS_ENDPOINT = "/statistics";
    public static final String LAST_PATH = "/last";

    private final CommandStatisticService commandStatisticService;

    @GetMapping(LAST_PATH)
    public ResponseEntity<StatisticsResult> getLastStatistic() {
        StatisticsResult lastStatistics = commandStatisticService.getLastStatistics();

        if (lastStatistics == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(lastStatistics);
    }

}
