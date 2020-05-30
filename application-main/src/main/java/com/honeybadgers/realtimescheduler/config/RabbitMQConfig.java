package com.honeybadgers.realtimescheduler.config;

import com.honeybadgers.realtimescheduler.services.RabbitMQReceiver;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.honeybadgers.realtimescheduler")
public class RabbitMQConfig {

    @Value("${dispatch.rabbitmq.dispatcherqueue}")
    String dispatcherqueue;

    @Value("${dispatch.rabbitmq.feedbackqueue}")
    String feedbackqueue;

    @Value("${dispatch.rabbitmq.dispatcherexchange}")
    String dispatcherExchange;

    @Value("${dispatch.rabbitmq.feedbackexchange}")
    String feedbackExchange;

    @Value("${dispatch.rabbitmq.dispatcherroutingkey}")
    private String dispatcherroutingkey;
    @Value("${dispatch.rabbitmq.feedbackroutingkey}")
    private String feedbackroutingkey;

    @Qualifier("dispatcherqueue")
    @Bean
    Queue dispatcherqueue() {
        return new Queue(dispatcherqueue, false);
    }
    @Qualifier("feedbackqueue")
    @Bean
    Queue feedbackqueue() {
        return new Queue(feedbackqueue, false);
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

    @Bean
    Binding dispatchbinding(@Qualifier("dispatcherqueue") Queue dispatcherqueue, @Qualifier("dispatcherExchange") DirectExchange exchange) {
        return BindingBuilder.bind(dispatcherqueue).to(exchange).with(dispatcherroutingkey);
    }
    @Bean
    Binding feedbackbinding(@Qualifier("feedbackqueue") Queue feedbackqueue, @Qualifier("feedbackExchange")DirectExchange exchange) {
        return BindingBuilder.bind(feedbackqueue).to(exchange).with(feedbackroutingkey);
    }
    @Bean
    SimpleMessageListenerContainer dispatchcontainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter dispatchlistenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(dispatcherqueue);
        container.setMessageListener(dispatchlistenerAdapter);

        return container;
    }
    @Bean
    SimpleMessageListenerContainer feedbackcontainer(ConnectionFactory connectionFactory,
                                             MessageListenerAdapter feedbacklistenerAdapter) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setQueueNames(feedbackqueue);
        container.setMessageListener(feedbacklistenerAdapter);

        return container;
    }

    @Bean
    MessageListenerAdapter feedbacklistenerAdapter(RabbitMQReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveFeedback");
    }
    @Bean
    MessageListenerAdapter dispatchlistenerAdapter(RabbitMQReceiver receiver) {
        return new MessageListenerAdapter(receiver, "receiveTask");
    }
}
