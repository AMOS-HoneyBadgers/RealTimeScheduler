package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.models.model.Task;

public interface IFeedbackConsumer {

    /**
     * Methods which is called if feedback from the feedback queue is received in the scheduler.
     * Feedback is processed (parallelism degree and sequential check) and afterwards a trigger is send to the
     * scheduler to reschedule the waiting tasks. Catches several transaction exceptions and retries if transaction fails
     * (due to concurrency update/read)
     *
     * @param taskid task id of the received task
     * @throws InterruptedException if sleep is interrupted
     */
    void receiveFeedbackFromDispatcher(String taskid) throws InterruptedException;

    /**
     * Handles the feedback. Decreases the parallelism degree and decreases the sequentialIndexNumber of the group
     *
     * @param taskId to be processed
     */
    void processFeedback(String taskId);

    /**
     * Increases the LastIndexNumber of the corresponding group of the task
     *
     * @param currentTask task which is in a group of sequential tasks
     */
    void checkAndSetSequentialAndIndexNumber(Task currentTask);

    /**
     * Decreases the parallelism degree of the corresponding group of the task (including ancestor groups)
     *
     * @param currentTask task which is in a group of parallel tasks
     */
    void checkAndSetParallelismDegree(Task currentTask);
}
