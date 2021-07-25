package org.vitalii.vorobii.entity;

public enum CommandType {
    SQRT,
    COS,
    SIN,
    POW,
    EUCLID_DISTANCE;

    public static CommandType fromName(String name) {
        CommandType[] commandTypes = CommandType.values();

        for (CommandType commandType : commandTypes) {
            String commandTypeName = commandType.name();

            if (commandTypeName.equals(name)) {
                return commandType;
            }
        }
        return null;
    }

}
