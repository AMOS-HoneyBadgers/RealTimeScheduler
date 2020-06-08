package com.honeybadgers.communication.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.amqp.SimpleRabbitListenerContainerFactoryConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.honeybadgers.communication")
public class RabbitMQConfig {

    @Value("${dispatch.rabbitmq.dispatcherqueue}")
    String dispatcherqueue;

    @Value("${dispatch.rabbitmq.feedbackqueue}")
    String feedbackqueue;

    @Value("${dispatch.rabbitmq.tasksqueue}")
    String tasksqueue;

    @Value("${dispatch.rabbitmq.priorityqueue}")
    String priorityqueue;

    @Value("${dispatch.rabbitmq.dispatcherexchange}")
    String dispatcherExchange;

    @Value("${dispatch.rabbitmq.feedbackexchange}")
    String feedbackExchange;

    @Value("${dispatch.rabbitmq.tasksexchange}")
    String tasksExchange;
    @Value("${dispatch.rabbitmq.priorityexchange}")
    String priorityExchange;

    @Value("${dispatch.rabbitmq.dispatcherroutingkey}")
    private String dispatcherroutingkey;
    @Value("${dispatch.rabbitmq.feedbackroutingkey}")
    private String feedbackroutingkey;
    @Value("${dispatch.rabbitmq.tasksroutingkey}")
    private String tasksroutingkey;
    @Value("${dispatch.rabbitmq.priorityroutingkey}")
    private String priorityroutingkey;

    @Qualifier("dispatcherqueue")
    @Bean
    Queue dispatcherqueue() {
        return new Queue(dispatcherqueue, true);
    }

    @Qualifier("feedbackqueue")
    @Bean
    Queue feedbackqueue() {
        return new Queue(feedbackqueue, true);
    }

    @Qualifier("taskqueue")
    @Bean
    Queue tasksqueue() {
        return new Queue(tasksqueue, true);
    }
    @Qualifier("priorityqueue")
    @Bean
    Queue priorityqueue() {
        return new Queue(priorityqueue, true);
    }

    @Qualifier("dispatcherExchange")
    @Bean
    DirectExchange dispatcherexchange() {
        return new DirectExchange(dispatcherExchange);
    }

    @Qualifier("feedbackExchange")
    @Bean
    DirectExchange feedbackexchange() {
        return new DirectExchange(feedbackExchange);
    }

    @Qualifier("tasksExchange")
    @Bean
    DirectExchange tasksexchange() {
        return new DirectExchange(tasksExchange);
    }

    @Qualifier("priorityExchange")
    @Bean
    DirectExchange priorityExchange() {
        return new DirectExchange(priorityExchange);
    }

    @Bean
    Binding dispatchbinding(@Qualifier("dispatcherqueue") Queue dispatcherqueue, @Qualifier("dispatcherExchange") DirectExchange exchange) {
        return BindingBuilder.bind(dispatcherqueue).to(exchange).with(dispatcherroutingkey);
    }
    @Bean
    Binding feedbackbinding(@Qualifier("feedbackqueue") Queue feedbackqueue, @Qualifier("feedbackExchange")DirectExchange exchange) {
        return BindingBuilder.bind(feedbackqueue).to(exchange).with(feedbackroutingkey);
    }
    @Bean
    Binding tasksbinding(@Qualifier("tasksqueue") Queue tasksqueue, @Qualifier("tasksExchange")DirectExchange exchange) {
        return BindingBuilder.bind(tasksqueue).to(exchange).with(tasksroutingkey);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory priorityContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory dispatchcontainerfactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory taskcontainerFactory(ConnectionFactory connectionFactory, SimpleRabbitListenerContainerFactoryConfigurer configurer) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        configurer.configure(factory, connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }



    @Bean
    public SimpleRabbitListenerContainerFactory feedbackcontainerfactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(producerJackson2MessageConverter());
        return factory;
    }

    @Bean
    public RabbitTemplate rabbitTemplate(final ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(producerJackson2MessageConverter());
        return rabbitTemplate;
    }

    @Bean
    public Jackson2JsonMessageConverter producerJackson2MessageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
