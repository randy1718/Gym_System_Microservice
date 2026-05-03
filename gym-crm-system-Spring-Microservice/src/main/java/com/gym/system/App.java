package com.gym.system;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class App {
    public static void main( String[] args ){
        Logger logger = LoggerFactory.getLogger(App.class);
        logger.info("Initializing GYM System...");
        SpringApplication.run(App.class, args);
    }
}
