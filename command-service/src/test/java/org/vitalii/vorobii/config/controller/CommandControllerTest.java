package org.vitalii.vorobii.config.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.vitalii.vorobii.config.service.CommandService;
import org.vitalii.vorobii.config.entity.CommandDTO;
import org.vitalii.vorobii.config.exception.CommandProcessingException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class CommandControllerTest {

    @Mock
    private CommandService commandService;

    @InjectMocks
    private CommandController commandController;

    private CommandDTO command;

    private ResponseEntity<Boolean> response;

    @Test
    void testProcessCommand_whenProcessingFails() {
        givenCommand();
        givenCommandProcessingFails();
        whenProcessCommand();
        thenExpectBadRequest();
    }

    @Test
    void testProcessCommand_whenProcessingWorksFine() {
        givenCommand();
        givenCommandProcessingSucceeds();
        whenProcessCommand();
        thenExpectStatusOK();
    }

    private void givenCommand() {
        command = new CommandDTO();
    }

    private void givenCommandProcessingFails() {
        doThrow(CommandProcessingException.class)
                .when(commandService).processCommand(any(CommandDTO.class));
    }

    private void givenCommandProcessingSucceeds() {
        doNothing().when(commandService).processCommand(any(CommandDTO.class));
    }

    private void whenProcessCommand() {
        this.response = commandController.processCommand(command);
    }

    private void thenExpectBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    private void thenExpectStatusOK() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

}
