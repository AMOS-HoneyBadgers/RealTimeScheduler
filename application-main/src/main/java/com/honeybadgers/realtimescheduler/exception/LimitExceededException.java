package com.honeybadgers.realtimescheduler.exception;

public class LimitExceededException extends Exception {

    public LimitExceededException(String message) {
        super(message);
    }
}
