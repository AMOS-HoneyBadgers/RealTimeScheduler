package com.honeybadgers.realtimescheduler.config;

import com.honeybadgers.realtimescheduler.services.TaskConsumerRabbit;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQEventConsumerConfiguration {

    @Value("${dispatch.rabbitmq.tasksqueue}")
    private String taskqueue;


    @Value("${dispatch.rabbitmq.tasksroutingkey}")
    private String tasksroutingkey;

    @Value("${dispatch.rabbitmq.tasksexchange}")
    String tasksExchange;

    @Bean
    public Declarables topicBindings() {
        Queue queue = new Queue(taskqueue);
        DirectExchange topicExchange = new DirectExchange(tasksExchange);

        return new Declarables(
                queue,
                topicExchange,
                BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(tasksroutingkey)
        );
    }
}
