package com.honeybadgers.realtimescheduler.config;

import com.honeybadgers.communication.config.RabbitMQApplicationProperties;
import com.honeybadgers.realtimescheduler.services.TaskConsumerRabbit;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
//TODO Intellij shows that the class is not able to autowire rabbitMQApplicationProperties
//TODO but there are no test failures and the scheduler seems to do the work
//TODO check together what
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
@Configuration
public class RabbitMQEventConsumerConfiguration {

    @Autowired
    RabbitMQApplicationProperties rabbitMQApplicationProperties;

    @Bean
    public Declarables taskTopicBindings() {
        Queue queue = new Queue(rabbitMQApplicationProperties.getTasksqueue());
        DirectExchange topicExchange = new DirectExchange(rabbitMQApplicationProperties.getTasksexchange());

        return new Declarables(
                queue,
                topicExchange,
                BindingBuilder
                .bind(queue)
                .to(topicExchange)
                .with(rabbitMQApplicationProperties.getTasksroutingkey())
        );
    }

    @Bean
    public Declarables priorityTopicBindings() {
        Queue queue = new Queue(rabbitMQApplicationProperties.getPriorityqueue());
        DirectExchange topicExchange = new DirectExchange(rabbitMQApplicationProperties.getPriorityexchange());

        return new Declarables(
                queue,
                topicExchange,
                BindingBuilder
                        .bind(queue)
                        .to(topicExchange)
                        .with(rabbitMQApplicationProperties.getPriorityroutingkey())
        );
    }
}
