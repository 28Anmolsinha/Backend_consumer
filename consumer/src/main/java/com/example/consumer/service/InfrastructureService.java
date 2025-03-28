package com.example.consumer.service;



import com.example.consumer.entity.Complaint;
import com.example.consumer.repository.ComplaintRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfrastructureService {
    private static final Logger logger = LogManager.getLogger(InfrastructureService.class);

    @Autowired
    private ComplaintRepository complaintRepository;

    @Autowired
    private RedisService redisService;

    public void processComplaint(Complaint complaint) {
        logger.info("Processing infrastructure complaint: {}", complaint);

        try {

            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Infrastructure team is reviewing the complaint...");


            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Infrastructure team is working on resolving the issue...");


            Thread.sleep(2000);
            complaint.setDescription(complaint.getDescription() );
            logger.info("Infrastructure issue resolved successfully!");
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
