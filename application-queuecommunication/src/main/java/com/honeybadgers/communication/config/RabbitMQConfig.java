package com.honeybadgers.communication.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.honeybadgers.communication")
public class RabbitMQConfig {
    @Autowired
    RabbitMQApplicationProperties rabbitMQApplicationProperties;

    @Qualifier("dispatcherqueue")
    @Bean
    Queue dispatcherqueue() {
        return new Queue(rabbitMQApplicationProperties.dispatcherqueue, false);
    }

    @Qualifier("feedbackqueue")
    @Bean
    Queue feedbackqueue() {
        return new Queue(rabbitMQApplicationProperties.feedbackqueue, false);
    }

    @Qualifier("taskqueue")
    @Bean
    Queue tasksqueue() {
        return new Queue(rabbitMQApplicationProperties.tasksqueue, false);
    }
    @Qualifier("priorityqueue")
    @Bean
    Queue priorityqueue() {
        return new Queue(rabbitMQApplicationProperties.priorityqueue, false);
    }

    @Qualifier("dispatcherExchange")
    @Bean
    DirectExchange dispatcherexchange() {
        return new DirectExchange(rabbitMQApplicationProperties.dispatcherexchange);
    }

    @Qualifier("feedbackExchange")
    @Bean
    DirectExchange feedbackexchange() {
        return new DirectExchange(rabbitMQApplicationProperties.feedbackexchange);
    }

    @Qualifier("tasksExchange")
    @Bean
    DirectExchange tasksexchange() {
        return new DirectExchange(rabbitMQApplicationProperties.tasksexchange);
    }

    @Qualifier("priorityExchange")
    @Bean
    DirectExchange priorityExchange() {
        return new DirectExchange(rabbitMQApplicationProperties.priorityexchange);
    }

    @Bean
    Binding dispatchbinding(@Qualifier("dispatcherqueue") Queue dispatcherqueue, @Qualifier("dispatcherExchange") DirectExchange exchange) {
        return BindingBuilder.bind(dispatcherqueue).to(exchange).with(rabbitMQApplicationProperties.dispatcherroutingkey);
    }
    @Bean
    Binding feedbackbinding(@Qualifier("feedbackqueue") Queue feedbackqueue, @Qualifier("feedbackExchange")DirectExchange exchange) {
        return BindingBuilder.bind(feedbackqueue).to(exchange).with(rabbitMQApplicationProperties.feedbackroutingkey);
    }
    @Bean
    Binding tasksbinding(@Qualifier("tasksqueue") Queue tasksqueue, @Qualifier("tasksExchange")DirectExchange exchange) {
        return BindingBuilder.bind(tasksqueue).to(exchange).with(rabbitMQApplicationProperties.tasksroutingkey);
    }
    @Bean
    SimpleMessageListenerContainer dispatchcontainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(rabbitMQApplicationProperties.dispatcherqueue);

        return container;
    }
    @Bean
    SimpleMessageListenerContainer feedbackcontainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(rabbitMQApplicationProperties.feedbackqueue);

        return container;
    }
    @Bean
    SimpleMessageListenerContainer priorityContainer(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(rabbitMQApplicationProperties.priorityqueue);

        return container;
    }

    @Bean
    public SimpleRabbitListenerContainerFactory taskcontainerfactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        return factory;
    }
}
