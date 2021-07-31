package org.vitalii.vorobii.service;

import org.vitalii.vorobii.exception.ExecutionFailedException;

import java.util.Map;
import java.util.Optional;

public class SinCommandExecutor implements CommandExecutor {
    private static final String NUMBER = "number";

    @Override
    public Object execute(Map<String, String> parameters) {
        String numberAsString = parameters.get(NUMBER);

        try {
            Double number = fetchNumber(numberAsString);

            return Math.sin(number);
        }
        catch (NumberFormatException numberFormatException) {
            throw new ExecutionFailedException();
        }
    }

    private Double fetchNumber(String text) {
        return Optional
                .ofNullable(text)
                .map(Double::parseDouble)
                .orElse(null);
    }

}
