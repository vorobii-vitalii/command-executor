package org.vitalii.vorobii.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.vitalii.vorobii.service.CommandStatisticService;

@EnableScheduling
@RequiredArgsConstructor
@Configuration
public class ScheduledConfig {
    private final CommandStatisticService commandStatisticService;

    @Scheduled(fixedRate = 5000L)
    public void update() {
        commandStatisticService.recalculate();
    }

}
