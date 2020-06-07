package com.honeybadgers.realtimescheduler.services;

import com.honeybadgers.communication.model.TaskQueueModel;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class TaskConsumer {

    @RabbitListener(queues="tasks", containerFactory = "taskcontainerfactory")
    public void receiveTask(String message) {
        System.out.println("Received message '{}'" + message);
    }

    @RabbitListener(queues="priority", containerFactory = "priorityContainerFactory")
    public void receiveTaskQueueModel(TaskQueueModel message) {
        System.out.println("Received message '{}'" + message.toString());
    }

}
