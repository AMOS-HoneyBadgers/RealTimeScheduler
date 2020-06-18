package com.honeybadgers.realtimescheduler.consumer;

import com.honeybadgers.communication.model.TaskQueueModel;
import com.honeybadgers.realtimescheduler.services.impl.SchedulerService;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@EnableRabbit
@Service
public class TaskConsumer {

    @Autowired
    SchedulerService service;

    @RabbitListener(queues="tasks", containerFactory = "taskcontainerFactory")
    public void receiveTask(String message) {
        System.out.println("Received message in TaskConsumer with id:" + message);
        System.out.println("Step 1: scheduling Task");
        try {
            service.scheduleTask(message);
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }

    }

    @RabbitListener(queues="priority", containerFactory = "priorityContainerFactory")
    public void receiveTaskQueueModel(TaskQueueModel message) {
        System.out.println("Received message '{}'" + message.toString());
    }




}
