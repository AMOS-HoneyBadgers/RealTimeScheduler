package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.model.TaskQueueModel;

public interface IMockDispatcherConsumer {

    /**
     * Mocked the dispatcher in the scheduler instance. This method is called when a scheduler sends a task to the dispatcher
     * Sends feedback back to scheduler. In production there should be a dispatcher which should replace this
     *
     * @param task TaskQueueModel received from queue
     */
    void receiveTaskFromSchedulerMockDispatcher(TaskQueueModel task);
}
