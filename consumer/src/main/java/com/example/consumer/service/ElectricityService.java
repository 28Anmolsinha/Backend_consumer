package com.example.consumer.service;


import com.example.consumer.entity.Complaint;
import com.example.consumer.repository.ComplaintRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectricityService {
    private static final Logger logger = LogManager.getLogger(ElectricityService.class);

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private RedisService redisService;

    public void processComplaint(Complaint complaint) {
        logger.info("Processing electricity complaint: {}", complaint);

        try {

            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Electricity team has been assigned to the complaint.");


            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Investigation underway for electricity issue...");


            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Electricity issue has been resolved successfully!");
        }
        catch (InterruptedException e) {
            logger.error("Error in processing complaint", e);
            Thread.currentThread().interrupt();
        }


        complaintRepository.save(complaint);
          redisService.saveComplaint(complaint);
        logger.info("Complaint saved to database and Redis cache.");
    }
}

