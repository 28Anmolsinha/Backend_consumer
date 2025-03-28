package com.example.consumer.config;

//package com.backend.newproject.config;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableRabbit
public class RabbitMQConfig {

    private static final Logger logger = LogManager.getLogger(RabbitMQConfig.class);

    // Use the same exchange and queue names as the old project
    public static final String EXCHANGE_NAME = "complaint_exchange";

    public static final String INFRASTRUCTURE_QUEUE = "infrastructure_queue";
    public static final String ELECTRICITY_QUEUE = "electricity_queue";
    public static final String LEGAL_QUEUE = "legal_queue";

    public static final String INFRASTRUCTURE_ROUTING_KEY = "complaint.infrastructure";
    public static final String ELECTRICITY_ROUTING_KEY = "complaint.electricity";
    public static final String LEGAL_ROUTING_KEY = "complaint.legal";

    @Bean
    public DirectExchange exchange() {
        logger.info("Using existing Direct Exchange: {}", EXCHANGE_NAME);
        return new DirectExchange(EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue infrastructureQueue() {
        logger.info("Using existing Queue: {}", INFRASTRUCTURE_QUEUE);
        return new Queue(INFRASTRUCTURE_QUEUE, true);
    }

    @Bean
    public Queue electricityQueue() {
        logger.info("Using existing Queue: {}", ELECTRICITY_QUEUE);
        return new Queue(ELECTRICITY_QUEUE, true);
    }

    @Bean
    public Queue legalQueue() {
        logger.info("Using existing Queue: {}", LEGAL_QUEUE);
        return new Queue(LEGAL_QUEUE, true);
    }

    @Bean
    public Binding infrastructureBinding(Queue infrastructureQueue, DirectExchange exchange) {
        return BindingBuilder.bind(infrastructureQueue).to(exchange).with(INFRASTRUCTURE_ROUTING_KEY);
    }

    @Bean
    public Binding electricityBinding(Queue electricityQueue, DirectExchange exchange) {
        return BindingBuilder.bind(electricityQueue).to(exchange).with(ELECTRICITY_ROUTING_KEY);
    }

    @Bean
    public Binding legalBinding(Queue legalQueue, DirectExchange exchange) {
        return BindingBuilder.bind(legalQueue).to(exchange).with(LEGAL_ROUTING_KEY);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        logger.info("Configuring Jackson2JsonMessageConverter for RabbitMQ");
        return new Jackson2JsonMessageConverter();
    }
}

