package com.honeybadgers.consumer;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.honeybadgers.consumer")
public class RabbitMQConfig {

    @Value("${dispatch.rabbitmq.tasksqueue}")
    String tasksqueue;
    @Value("${dispatch.rabbitmq.tasksexchange}")
    String tasksExchange;
    private String tasksroutingkey;


    @Qualifier("taskqueue")
    @Bean
    Queue tasksqueue() {
        return new Queue(tasksqueue, false);
    }

    @Qualifier("tasksExchange")
    @Bean
    DirectExchange tasksexchange() {
        return new DirectExchange(tasksExchange);
    }

    @Bean
    Binding tasksbinding(@Qualifier("tasksqueue") Queue tasksqueue, @Qualifier("tasksExchange")DirectExchange exchange) {
        return BindingBuilder.bind(tasksqueue).to(exchange).with(tasksroutingkey);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory taskcontainerfactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
