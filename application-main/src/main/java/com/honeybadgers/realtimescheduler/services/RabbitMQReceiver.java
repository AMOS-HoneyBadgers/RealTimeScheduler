package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Component;

@Component
public class RabbitMQReceiver {

    public void receiveTask(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }

    public void receiveFeedback(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }
}
