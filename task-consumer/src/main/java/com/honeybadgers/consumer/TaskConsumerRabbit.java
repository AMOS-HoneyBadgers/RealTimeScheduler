package com.honeybadgers.consumer;

import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.stereotype.Component;

import static org.springframework.amqp.core.ExchangeTypes.DIRECT;
@Component
@EnableRabbit
public class TaskConsumerRabbit {

    public int count = 0;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(value = "tasks"), exchange = @Exchange(value = "tasks.exchange", type = DIRECT), key="tasks.routingkey"))
    public void receiveTask(String message) {
        System.out.println("Received message '{}'" + message);
        count = count++;
    }

}
