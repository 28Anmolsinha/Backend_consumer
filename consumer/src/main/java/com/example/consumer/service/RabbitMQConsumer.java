package com.example.consumer.service;

import com.example.consumer.config.RabbitMQConfig;
import com.example.consumer.entity.Complaint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RabbitMQConsumer {
    private static final Logger logger = LogManager.getLogger(RabbitMQConsumer.class);

    @Autowired
    private InfrastructureService infrastructureService;

    @Autowired
    private ElectricityService electricityService;

    @Autowired
    private LegalService legalService;

    @RabbitListener(queues = RabbitMQConfig.INFRASTRUCTURE_QUEUE)
    public void receiveInfrastructureComplaint(Complaint complaint) {
        logger.info("Received Infrastructure complaint: {}", complaint);
        infrastructureService.processComplaint(complaint);
    }

    @RabbitListener(queues = RabbitMQConfig.ELECTRICITY_QUEUE)
    public void receiveElectricityComplaint(Complaint complaint) {
        logger.info("Received Electricity complaint: {}", complaint);
        electricityService.processComplaint(complaint);
    }

    @RabbitListener(queues = RabbitMQConfig.LEGAL_QUEUE)
    public void receiveLegalComplaint(Complaint complaint) {
        logger.info("Received Legal complaint: {}", complaint);
        legalService.processComplaint(complaint);
    }
}
