package com.honeybadgers.models.exceptions;

public class TransactionRetriesExceeded extends Exception {

    public TransactionRetriesExceeded(String message) {
        super(message);
    }
}
