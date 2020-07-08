package com.honeybadgers.communication;

import com.honeybadgers.communication.model.TaskQueueModel;

public interface ICommunication {

    /**
     * Sends a task to the dispatcher in the RabbitMQ
     * @param task id of the task
     */
    void sendTaskToDispatcher(String task);

    /**
     * Sends feedback back to the scheduler if task is finished in the RabbitMQ
     * @param feedback id of the task which is finished
     * @return message for test purpose
     */
    String sendFeedbackToScheduler(String feedback);

    /**
     * Sends task to the tasks queue (from api call to scheduler) in the RabbitMQ
     * @param task id of the task
     */
    void sendTaskToTasksQueue(String task);

    /**
     * Sends a group to the tasks queue if a new group is created via group api
     * @param group id of the group
     */
    void sendGroupToTasksQueue(String group);

    /**
     * Sends a task to the priority queue (if "force" attribute is set)
     * @param task taskQueueModel with timestamps for dispatching and metadata
     */
    void sendTaskToPriorityQueue(TaskQueueModel task);
}
