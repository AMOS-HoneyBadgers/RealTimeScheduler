package com.honeybadgers.realtimescheduler.model;

public enum TaskStatusEnum {
    Waiting, Scheduled, Dispatched, Finished;

    public static TaskStatusEnum getFromString(String status) {
        if (status.compareToIgnoreCase("Waiting") == 0) {
            return Waiting;
        } else if (status.compareToIgnoreCase("Scheduled") == 0) {
            return Scheduled;
        } else if (status.compareToIgnoreCase("Dispatched") == 0) {
            return Dispatched;
        } else if (status.compareToIgnoreCase("Finished") == 0) {
            return Finished;
        } else {
            return Waiting;
        }
    }
}
