package com.example.consumer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@SpringBootApplication
public class ConsumerApplication {
	private static final Logger logger = LogManager.getLogger(ConsumerApplication.class);

	public static void main(String[] args) {

		SpringApplication.run(ConsumerApplication.class, args);
		logger.info("Ready to receive complaints from queue : ");
	}

}
