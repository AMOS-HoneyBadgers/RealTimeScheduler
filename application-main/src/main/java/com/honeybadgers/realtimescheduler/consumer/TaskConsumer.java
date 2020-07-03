package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
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
            service.scheduleTask("");
        } catch(Exception e) {
            logger.error(e.getStackTrace());
            logger.error(e.getMessage());
        }
    }

    @RabbitListener(queues="priority", containerFactory = "priorityContainerFactory")
    public void receiveTaskQueueModel(TaskQueueModel message) {
        logger.info("Task " + message + " from Priority Queue");
    }
}
