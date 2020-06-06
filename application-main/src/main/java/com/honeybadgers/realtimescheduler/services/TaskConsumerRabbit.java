package com.honeybadgers.realtimescheduler.services;

import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@EnableRabbit
public class TaskConsumerRabbit {

    @RabbitListener(queues="tasks")
    public void receiveTask(String message) {
        System.out.println("Received message '{}'" + message);
    }

}
