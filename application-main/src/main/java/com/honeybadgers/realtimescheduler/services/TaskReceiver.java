package com.honeybadgers.realtimescheduler.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import javax.jms.Destination;

import static com.honeybadgers.realtimescheduler.config.RmqConfig.TASK_QUEUE;

@Component
public class TaskReceiver {

    @JmsListener(destination ="mydest", containerFactory = "myFactory")
    public void receive(String task) {
        System.out.println("hello" + task);
    }
}
