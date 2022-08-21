package com.fakng.fakngagrgtr.configuration;

import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitConfig {

    @Bean
    public Queue notificationQueue() {
        return new Queue("notification.queue", false);
    }

    @Bean
    public Queue vacancyQueue() {
        return new Queue("vacancy.queue", false);
    }
}
