package com.honeybadgers.realtimescheduler.services;

public interface ICommunication {
    void sendTaskToDispatcher(String task);
    void sendFeedbackToScheduler(String feedback);
}
