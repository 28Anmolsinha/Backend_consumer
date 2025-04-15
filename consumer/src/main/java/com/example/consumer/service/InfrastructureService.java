package com.example.consumer.service;

import com.example.consumer.entity.Complaint;
import com.example.consumer.repository.ComplaintRepository;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.Tracer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class InfrastructureService {

    private static final Logger logger = LogManager.getLogger(LegalService.class);

    @Autowired private ComplaintRepository complaintRepository;
    @Autowired private RedisService redisService;
    @Autowired private Tracer tracer;

    public void processComplaint(Complaint complaint, String userId, Span backend2Span) {
        try {
            // Step 1: Backend2 span (already started in consumer and passed here)
            logger.info("Processing infrastructure complaint from user {}: {}", userId, complaint);
            Thread.sleep(2000);
            logger.info("Infrastructure team analyzing issue...");
            Thread.sleep(2000);
            logger.info("Infrastructure team fixing the issue...");
            Thread.sleep(2000);
            logger.info("Infrastructure issue resolved!");

        } catch (InterruptedException e) {
            backend2Span.recordException(e);
            Thread.currentThread().interrupt();
            logger.error("Interrupted during Infrastructure processing", e);
        } catch (Exception e) {
            backend2Span.recordException(e);
            logger.error("Error in Infrastructure service", e);
            throw e;
        } finally {
            backend2Span.end(); // End Backend2 span before MySQL
        }

        // Step 2: MySQL span (separate, not a child of Backend2)
        Span mysqlSpan = tracer.spanBuilder("MySQL")
                .setAttribute("db.system", "mysql")
                .setAttribute("db.operation", "insert")
                .startSpan();
        try {
            complaintRepository.save(complaint);
            mysqlSpan.setAttribute("mysql.complaint_id", complaint.getId().toString());
        } catch (Exception e) {
            mysqlSpan.recordException(e);
            logger.error("Error saving complaint to MySQL", e);
            throw e;
        } finally {
            mysqlSpan.end();
        }

        // Step 3: Redis span (separate, sibling of MySQL)
        Span redisSpan = tracer.spanBuilder("Redis")
                .setAttribute("cache.system", "redis")
                .setAttribute("cache.operation", "saveComplaint")
                .startSpan();
        try {
            redisService.saveComplaint(complaint);
            redisSpan.setAttribute("redis.status", "saved");
        } catch (Exception e) {
            redisSpan.recordException(e);
            logger.error("Error saving complaint to Redis", e);
            throw e;
        } finally {
            redisSpan.end();
        }

        logger.info("Complaint saved to MySQL and Redis successfully.");
    }
}
