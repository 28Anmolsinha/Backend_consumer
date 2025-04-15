package com.example.consumer.service;

import com.example.consumer.entity.Complaint;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisService {
    private static final Logger logger = LogManager.getLogger(RedisService.class);

    private final RedisTemplate<String, Object> redisTemplate;

    private static final String COMPLAINTS_KEY = "top_complaints";

    public RedisService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void saveComplaint(Complaint complaint) {
        logger.info("Saving complaint to Redis: {}", complaint);
        ListOperations<String, Object> listOps = redisTemplate.opsForList();
        listOps.leftPush(COMPLAINTS_KEY, complaint);

        if (listOps.size(COMPLAINTS_KEY) > 3) {
            Object removedComplaint = listOps.rightPop(COMPLAINTS_KEY);
            logger.info("Removed old complaint from Redis: {}", removedComplaint);
        }

        List<Object> currentComplaints = listOps.range(COMPLAINTS_KEY, 0, -1);
        logger.info("Updated Redis List: {}", currentComplaints);
    }

    public List<Object> getTopComplaints() {
        logger.info("Fetching top complaints from Redis.");
        List<Object> topComplaints = redisTemplate.opsForList().range(COMPLAINTS_KEY, 0, 2);
        logger.info("Top complaints retrieved: {}", topComplaints);
        return topComplaints;
    }
}
