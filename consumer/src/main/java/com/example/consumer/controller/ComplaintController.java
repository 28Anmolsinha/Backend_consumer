package com.example.consumer.controller;

import com.example.consumer.service.RedisService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;


@RestController
@CrossOrigin(origins = "http://10.200.208.98:31382")
@RequestMapping("/api/complaints")
public class ComplaintController {
    private static final Logger logger = LogManager.getLogger(ComplaintController.class);

    private final RedisService redisService;

    public ComplaintController(RedisService redisService) {
        this.redisService = redisService;
    }

    @GetMapping("/top")
    public List<Object> getTopComplaints() {
        logger.info("Fetching top complaints :");
        return redisService.getTopComplaints();
    }
}
