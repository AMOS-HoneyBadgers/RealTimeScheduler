package com.honeybadgers.realtimescheduler.consumer;

public interface IMockDispatcherConsumer {

    /**
     * Mocked the dispatcher in the scheduler instance. This method is called when a scheduler sends a task to the dispatcher
     * Sends feedback back to scheduler. In production there should be a dispatcher which should replace this
     * @param message task id which is received
     */
    void receiveTaskFromSchedulerMockDispatcher(String message);
}
