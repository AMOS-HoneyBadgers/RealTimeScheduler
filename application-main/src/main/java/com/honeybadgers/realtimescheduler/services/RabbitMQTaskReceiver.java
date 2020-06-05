package com.honeybadgers.realtimescheduler.services;

import org.springframework.stereotype.Component;

@Component
public class RabbitMQTaskReceiver {

    /*@Autowired
    ICommunication communication;

    public RabbitMQTaskReceiver() {

    }

    public RabbitMQTaskReceiver(ICommunication communication) {
        this.communication = communication;
    }

    public void receiveTask(String in) {
        System.out.println(" [x] Received '" + in + "'");
        try {
            workTask();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        communication.sendFeedbackToScheduler("feedback sent");
    }

    public void workTask() throws InterruptedException {
        long waitTime = (long) (Math.random() * ((10000 - 1000) + 1));
        Thread.sleep(waitTime);
    }

    public void receiveFeedback(String in) {
        System.out.println(" [x] Received '" + in + "'");
        changeTaskStatus(in);
    }

    public void changeTaskStatus(String in) {
        // do database operation
    }

    public void receiveTaskFromEventQueue(String in) {
        System.out.println(" [x] Received '" + in + "'");
    }*/
}
