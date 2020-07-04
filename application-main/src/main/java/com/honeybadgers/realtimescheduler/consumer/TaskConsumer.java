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

    @RabbitListener(queues="priority", containerFactory = "priorityContainerFactory")
    public void receiveTaskQueueModel(TaskQueueModel message) {
        logger.info("Task " + message + " from Priority Queue");
    }
}
