package com.honeybadgers.taskapi.service;

public interface ISendTasksToTaksQueue {
    void sendTasktoTasksQueue(String groupId);
}
