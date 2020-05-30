package com.honeybadgers.models;

public enum TypeFlagEnum {
    Batch, Realtime;

    public static TypeFlagEnum getFromString(String type) {
        if (type == null)
            return null;
        if (type.compareToIgnoreCase("Batch") == 0) {
            return Batch;
        } else if (type.compareToIgnoreCase("Realtime") == 0) {
            return Realtime;
        } else {
            return Batch;
        }
    }
}
