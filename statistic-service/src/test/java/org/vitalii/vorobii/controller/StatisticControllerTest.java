package org.vitalii.vorobii.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.vitalii.vorobii.entity.StatisticsResult;
import org.vitalii.vorobii.service.CommandStatisticService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StatisticControllerTest {

    @Mock
    private CommandStatisticService commandStatisticService;

    @InjectMocks
    private StatisticController statisticController;

    @Mock
    private StatisticsResult statisticsResult;

    private ResponseEntity<StatisticsResult> response;

    @Test
    void testGetStatistics_givenStatisticNotYetPresent() {
        givenStatisticNotPresent();
        whenCallEndpoint();
        verifyResponseStatusIsNotFound();
        verifyResponseBodyIsEmpty();
    }

    @Test
    void testGetStatistics_givenStatisticPresent() {
        givenStatisticIsFound();
        whenCallEndpoint();
        verifyResponseStatusIsOK();
        verifyResponseBody();
    }

    private void verifyResponseStatusIsOK() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private void verifyResponseBody() {
        assertEquals(statisticsResult, response.getBody());
    }

    private void verifyResponseBodyIsEmpty() {
        assertNull(response.getBody());
    }

    private void verifyResponseStatusIsNotFound() {
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    private void whenCallEndpoint() {
       this.response  = statisticController.getLastStatistic();
    }

    private void givenStatisticNotPresent() {
        when(commandStatisticService.getLastStatistics())
                .thenReturn(null);
    }

    private void givenStatisticIsFound() {
        when(commandStatisticService.getLastStatistics())
                .thenReturn(statisticsResult);
    }

}