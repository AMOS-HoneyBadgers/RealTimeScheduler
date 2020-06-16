package com.honeybadgers.models.model;

public enum TypeFlagEnum {
    Batch, Realtime;

    public static TypeFlagEnum getFromString(String type) throws UnknownEnumException {
        if (type == null)
            return null;
        if (type.compareToIgnoreCase("Batch") == 0) {
            return Batch;
        } else if (type.compareToIgnoreCase("Realtime") == 0) {
            return Realtime;
        } else {
            throw new UnknownEnumException("Unknown Enum: " + type);
        }
    }
}
