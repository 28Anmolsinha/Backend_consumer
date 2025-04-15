package com.example.consumer.service;

import com.example.consumer.entity.Complaint;
import com.example.consumer.repository.ComplaintRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.api.trace.Tracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ElectricityService {

    private static final Logger logger = LogManager.getLogger(ElectricityService.class);

    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private RedisService redisService;
    @Autowired private Tracer tracer;

    public void processComplaint(Complaint complaint, String userId, Span backend2Span) {
        try {
            // Step 1: Backend2 span is already started from caller
            logger.info("Processing electricity complaint from user {}: {}", userId, complaint);

            Thread.sleep(2000);
            logger.info("Electricity team analyzing issue...");
            Thread.sleep(2000);
            logger.info("Electricity team fixing the issue...");
            Thread.sleep(2000);
            logger.info("Electricity issue resolved!");

        } catch (InterruptedException e) {
            backend2Span.recordException(e);
            backend2Span.setStatus(StatusCode.ERROR, "InterruptedException in electricity service");
            Thread.currentThread().interrupt();
            logger.error("Interrupted during electricity processing", e);
        } catch (Exception e) {
            backend2Span.recordException(e);
            backend2Span.setStatus(StatusCode.ERROR, "Unexpected error in electricity service");
            logger.error("Error in electricity service", e);
            throw e;
        } finally {
            backend2Span.end(); // End Backend2 span before siblings
        }

        // Step 2: MySQL span
        Span mysqlSpan = tracer.spanBuilder("MySQL")
                .setAttribute("db.system", "mysql")
                .setAttribute("db.operation", "insert")
                .startSpan();
        try {
            complaintRepository.save(complaint);
            mysqlSpan.setAttribute("mysql.complaint_id", complaint.getId().toString());
        } catch (Exception e) {
            mysqlSpan.recordException(e);
            mysqlSpan.setStatus(StatusCode.ERROR, "Error saving to MySQL");
            logger.error("Error saving complaint to MySQL", e);
            throw e;
        } finally {
            mysqlSpan.end();
        }

        // Step 3: Redis span
        Span redisSpan = tracer.spanBuilder("Redis")
                .setAttribute("cache.system", "redis")
                .setAttribute("cache.operation", "saveComplaint")
                .startSpan();
        try {
            redisService.saveComplaint(complaint);
            redisSpan.setAttribute("redis.status", "saved");
        } catch (Exception e) {
            redisSpan.recordException(e);
            redisSpan.setStatus(StatusCode.ERROR, "Error saving to Redis");
            logger.error("Error saving complaint to Redis", e);
            throw e;
        } finally {
            redisSpan.end();
        }

        logger.info("Complaint saved to MySQL and Redis successfully.");
    }
}
