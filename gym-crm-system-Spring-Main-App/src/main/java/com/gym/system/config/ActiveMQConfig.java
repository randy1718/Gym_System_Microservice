package com.gym.system.config;

import org.apache.activemq.broker.region.Queue;
import org.apache.activemq.command.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.annotation.EnableJms;

@Configuration
@EnableJms
public class ActiveMQConfig {

    @Bean
    public ActiveMQQueue workloadQueue() {
        return new ActiveMQQueue("workload.queue");
    }

    @Bean
    public ActiveMQQueue deadLetterQueue() {
        return new ActiveMQQueue("workload.dlq");
    }
}