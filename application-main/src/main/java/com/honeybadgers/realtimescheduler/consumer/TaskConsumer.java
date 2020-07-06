package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.TransactionException;
import org.hibernate.exception.LockAcquisitionException;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.CannotAcquireLockException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@EnableRabbit
@Service
public class TaskConsumer {

    static final Logger logger = LogManager.getLogger(TaskConsumer.class);

    @Autowired
    SchedulerService service;

    /**
     * This method is called when a task in the task queue is received from the task api in the scheduler.
     * Triggers the scheduling process and catches several transaction exceptions
     * @param taskid id of the received task
     */
    @RabbitListener(queues="tasks", containerFactory = "taskcontainerFactory")
    public void receiveTask(String taskid) {
        logger.info("Task " + taskid + " Step 1: received Task");
        try {
            service.scheduleTaskWrapper(taskid);
        } catch (JpaSystemException | TransactionException | CannotAcquireLockException | LockAcquisitionException exception){
            // TransactionException is nested ex of JpaSystemException and LockAcquisitionException is nested of CannotAcquireLockException
            logger.warn("Task " + taskid + " transaction exception in scheduleTaskWrapper" );
        } catch(Exception e) {
            logger.error(e.getMessage());
        }
    }

    /**
     * This method is called when a task is received in the priority queue (It should have the "force" attribute)
     * @param message id of the received task
     */
    @RabbitListener(queues="priority", containerFactory = "priorityContainerFactory")
    public void receiveTaskQueueModel(TaskQueueModel message) {
        logger.info("Task " + message + " from Priority Queue");
        // TODO: Send to dispatcher
    }
}
