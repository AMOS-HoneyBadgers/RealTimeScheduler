package com.honeybadgers.realtimescheduler.config;


import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@ComponentScan
@EnableAutoConfiguration
@EnableScheduling
public class StockQuoter {

    /*private static final Log log = LogFactory.getLog(StockQuoter.class);

    @Bean
    public JmsTemplate jmsTemplate() {
        JmsTemplate jmsTemplate = new JmsTemplate();
        jmsTemplate.setDefaultDestination(jmsDestination());
        return jmsTemplate;
    }

    @Bean()
    public Destination jmsDestination() {
        RMQDestination jmsDestination = new RMQDestination();
        jmsDestination.setDestinationName("dispatch.queue");
        jmsDestination.setAmqp(true);
        jmsDestination.setAmqpQueueName("${dispatch.rabbitmq.queue}");
        jmsDestination.setAmqpExchangeName("${dispatch.rabbitmq.exchange}");
        jmsDestination.setAmqpRoutingKey("${dispatch.rabbitmq.routingkey}");
        return jmsDestination;
    }

    @Bean
    ConnectionFactory connectionFactory() {
        return new RMQConnectionFactory();
    }

    @Scheduled(fixedRate = 5000L) // every 5 seconds
    public void publishQuote() {
        // Coerce a javax.jms.MessageCreator
        MessageCreator messageCreator = (Session session) -> {
            return session.createObjectMessage(
                    "asdasd");
        };
        // And publish to RabbitMQ using Spring's JmsTemplate
        jmsTemplate().send("dispatch.queue", messageCreator);
    }

    public static void main(String[] args) {
        SpringApplication.run(StockQuoter.class, args);
    }*/
}
