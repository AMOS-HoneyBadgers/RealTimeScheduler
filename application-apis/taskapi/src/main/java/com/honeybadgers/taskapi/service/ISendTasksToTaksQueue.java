package com.honeybadgers.taskapi.service;

public interface ISendTasksToTaksQueue {
    void sendTaskToTaskQueue(String task);
}
