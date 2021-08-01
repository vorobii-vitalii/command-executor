package org.vitalii.vorobii.config.service;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.vitalii.vorobii.config.dao.DoneCommandRepository;
import org.vitalii.vorobii.config.entity.MongoDoneCommand;
import org.vitalii.vorobii.entity.DoneCommand;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoneCommandServiceImpl implements DoneCommandService {

    private final DoneCommandRepository doneCommandRepository;

    @Override
    public void insertCommand(@NonNull DoneCommand doneCommand) {

        var mongoDoneCommand = convertFromDto(doneCommand);

        doneCommandRepository.save(mongoDoneCommand);
    }

    @Override
    public List<MongoDoneCommand> getByClientId(@NonNull String clientId) {

        var commands =
                doneCommandRepository.findByClientId(clientId);

        return Optional.ofNullable(commands).orElse(Collections.emptyList());
    }

    private MongoDoneCommand convertFromDto(DoneCommand doneCommand) {
        var command = doneCommand.getCommand();

        return MongoDoneCommand
                .builder()
                .executionResult(doneCommand.getExecutionResult())
                .clientId(command.getClientId())
                .requestedAt(command.getRequestedAt())
                .executionStatus(doneCommand.getResultType())
                .commandType(command.getCommandType())
                .params(command.getParams())
                .build();
    }

}
