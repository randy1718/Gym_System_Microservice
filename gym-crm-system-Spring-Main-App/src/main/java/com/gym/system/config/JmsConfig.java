package com.gym.system.config;

import jakarta.jms.ConnectionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jms.config.DefaultJmsListenerContainerFactory;
import org.springframework.jms.support.converter.JacksonJsonMessageConverter;
import org.springframework.jms.support.converter.MessageConverter;
import org.springframework.jms.support.converter.MessageType;

@Configuration
public class JmsConfig {

    private static final Logger logger = LoggerFactory.getLogger(JmsConfig.class);

    @Value("${app.jms.concurrency}")
    private String concurrency;

    @Bean
    public JacksonJsonMessageConverter jacksonJmsMessageConverter() {

        JacksonJsonMessageConverter converter =
                new JacksonJsonMessageConverter();

        converter.setTargetType(MessageType.TEXT);
        converter.setTypeIdPropertyName("_type");

        return converter;
    }

    @Bean
    public DefaultJmsListenerContainerFactory jmsListenerContainerFactory(
            @Qualifier("jmsConnectionFactory") ConnectionFactory connectionFactory,
            MessageConverter converter) {

        DefaultJmsListenerContainerFactory factory =
                new DefaultJmsListenerContainerFactory();

        factory.setConnectionFactory(connectionFactory);

        factory.setMessageConverter(converter);

        factory.setErrorHandler(t ->
                logger.error("JMS Error occurred", t));

        factory.setConcurrency(concurrency);

        return factory;
    }


}