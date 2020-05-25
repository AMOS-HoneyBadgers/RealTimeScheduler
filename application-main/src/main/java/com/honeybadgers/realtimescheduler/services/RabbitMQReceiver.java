package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver {

    public void receive(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }
}
