package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.model.TaskQueueModel;

public interface ITaskConsumer {

    /**
     * This method is called when a task in the task queue is received from the task api in the scheduler.
     * Triggers the scheduling process and catches several transaction exceptions
     * @param taskid id of the received task
     */
    void receiveTask(String taskid);

    /**
     * This method is called when a task is received in the priority queue (It should have the "force" attribute)
     * @param message id of the received task
     */
    void receiveTaskQueueModel(TaskQueueModel message);
}
