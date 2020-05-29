package com.honeybadgers.realtimescheduler.services.impl;

import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver {

    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }
}
