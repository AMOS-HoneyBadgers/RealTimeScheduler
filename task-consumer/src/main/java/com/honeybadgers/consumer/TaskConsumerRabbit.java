package com.honeybadgers.consumer;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class TaskConsumerRabbit {

    public int count = 0;

    @RabbitListener(queues="tasks", containerFactory = "taskcontainerfactory")
    public void receiveTask(String message) {
        System.out.println("Received message '{}'" + message);
        count = count++;
    }

}
