package com.honeybadgers.communication.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Configuration
@ConfigurationProperties(value = "dispatch.rabbitmq")
public class RabbitMQApplicationProperties {
    String dispatcherqueue;
    String feedbackqueue;
    String tasksqueue;
    String priorityqueue;

    String dispatcherexchange;
    String feedbackexchange;
    String tasksexchange;
    String priorityexchange;

    String dispatcherroutingkey;
    String feedbackroutingkey;
    String tasksroutingkey;
    String priorityroutingkey;

    @Override
    public String toString() {
        return "RabbitMQApplicationProperties{" +
                "dispatcherqueue='" + dispatcherqueue + '\'' +
                ", feedbackqueue='" + feedbackqueue + '\'' +
                ", tasksqueue='" + tasksqueue + '\'' +
                ", priorityqueue='" + priorityqueue + '\'' +
                ", dispatcherexchange='" + dispatcherexchange + '\'' +
                ", feedbackexchange='" + feedbackexchange + '\'' +
                ", tasksexchange='" + tasksexchange + '\'' +
                ", priorityexchange='" + priorityexchange + '\'' +
                ", dispatcherroutingkey='" + dispatcherroutingkey + '\'' +
                ", feedbackroutingkey='" + feedbackroutingkey + '\'' +
                ", tasksroutingkey='" + tasksroutingkey + '\'' +
                ", priorityroutingkey='" + priorityroutingkey + '\'' +
                '}';
    }
}
