package com.honeybadgers.realtimescheduler.services;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class TaskConsumerRabbit {

    public int count = 0;

    @RabbitListener(queues="tasks")
    static final Logger logger = LogManager.getLogger(TaskConsumerRabbit.class);

    @RabbitListener(queues="tasks", containerFactory = "taskcontainerFactory")
    public void receiveTask(String message) {
        System.out.println("Received message '{}'" + message);
        count = count++;
        logger.info("Received message '{}'" + message);
    }

    @RabbitListener(queues="priority", containerFactory = "priorityContainerFactory")
    public void receiveTaskQueueModel(TaskQueueModel message) {
        logger.info("Received message '{}'" + message.toString());
    }

}
