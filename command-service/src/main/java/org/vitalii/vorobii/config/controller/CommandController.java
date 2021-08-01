package org.vitalii.vorobii.config.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.vitalii.vorobii.config.entity.CommandDTO;
import org.vitalii.vorobii.config.entity.MongoDoneCommand;
import org.vitalii.vorobii.config.exception.CommandProcessingException;
import org.vitalii.vorobii.config.service.CommandService;
import org.vitalii.vorobii.config.service.DoneCommandService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping(CommandController.COMMANDS_ENDPOINT)
@RequiredArgsConstructor
public class CommandController {
    public static final String COMMANDS_ENDPOINT = "/commands";
    private static final Boolean WAS_PROCESSED = Boolean.TRUE;
    private static final Boolean WAS_NOT_PROCESSED = Boolean.FALSE;

    private final CommandService commandService;
    private final DoneCommandService doneCommandService;

    @GetMapping("/{id}")
    public ResponseEntity<List<MongoDoneCommand>> getByClientId(@PathVariable("id") String id) {

        var commands = doneCommandService.getByClientId(id);

        return ResponseEntity.ok(commands);
    }

    @PostMapping
    public ResponseEntity<Boolean> processCommand(@Valid @RequestBody CommandDTO command) {

        try {
            commandService.processCommand(command);
        }
        catch (CommandProcessingException exception) {
            return ResponseEntity.badRequest().body(WAS_NOT_PROCESSED);
        }

        return ResponseEntity.ok(WAS_PROCESSED);
    }

}
