package com.example.consumer.service;

import com.example.consumer.entity.Complaint;
import com.example.consumer.repository.ComplaintRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LegalService {
    private static final Logger logger = LogManager.getLogger(LegalService.class);

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private RedisService redisService;

    public void processComplaint(Complaint complaint) {
        logger.info("Processing legal complaint: {}", complaint);

        try {

            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Legal team is checking documents and regulations...");


            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Legal team is analyzing case details...");


            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Legal issue resolved successfully!");

        } catch (InterruptedException e) {
            logger.error("Error in processing legal complaint", e);
            Thread.currentThread().interrupt();
        }


        complaintRepository.save(complaint);
        redisService.saveComplaint(complaint);
        logger.info("Complaint saved to database and Redis cache.");

    }
}
