package com.example.consumer.controller;

import com.example.consumer.repository.ComplaintRepository;
import com.example.consumer.service.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "http://10.200.208.98:31382")
@RequestMapping("/api/complaints")
public class ComplaintController {

    private static final Logger logger = LogManager.getLogger(ComplaintController.class);

    private final RedisService redisService;
    private final ComplaintRepository complaintRepository;

    // Constructor injection for both RedisService and ComplaintRepository
    public ComplaintController(RedisService redisService, ComplaintRepository complaintRepository) {
        this.redisService = redisService;
        this.complaintRepository = complaintRepository;
    }

    @GetMapping("/top")
    public List<Object> getTopComplaints() {
        logger.info("Fetching top complaints:");
        return redisService.getTopComplaints();
    }

    @GetMapping("/generate-id")
    public String generateComplaintId() {
        Integer maxId = complaintRepository.findMaxId();
        int nextId = (maxId != null) ? maxId + 1 : 1;
        logger.info("Generated complaint ID: " + nextId);
        return String.valueOf(nextId);
    }
}
