package com.honeybadgers.realtimescheduler.services;

public interface ICommunication {
    void sendTaskToDispatcher(String task);
    String sendFeedbackToScheduler(String feedback);
}