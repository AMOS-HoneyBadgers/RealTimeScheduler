package com.honeybadgers.communication;

import com.honeybadgers.communication.model.TaskQueueModel;

public interface ICommunication {
    void sendTaskToDispatcher(String task);
    String sendFeedbackToScheduler(String feedback);
    void sendTaskToTasksQueue(String task);
    void sendGroupToTasksQueue(String group);
    void sendTaskToPriorityQueue(TaskQueueModel task);
}
