package com.honeybadgers.communication;

public interface ICommunication {
    void sendTaskToDispatcher(String task);
    String sendFeedbackToScheduler(String feedback);
    void sendTaskToTaskQueue(String task);
}
