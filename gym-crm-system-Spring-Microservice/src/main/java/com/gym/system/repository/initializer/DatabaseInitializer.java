package com.gym.system.repository.initializer;
import java.io.InputStream;

import jakarta.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gym.system.service.DatabaseInitService;

@Component
@PropertySource("classpath:application.properties")
@Profile({"dev", "local"})
public class DatabaseInitializer {
    @Value("${storage.data.path}")
    private String storageDataPath;

    private static final Logger logger = LoggerFactory.getLogger(DatabaseInitializer.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final DatabaseInitService initService;

    public DatabaseInitializer(DatabaseInitService initService) {
        this.initService = initService;
    }

    @PostConstruct
    public void init() {
        JsonNode rootNode = loadJson();
        initService.initDatabase(rootNode);
    }

    private JsonNode loadJson() {
        try {
            String path = storageDataPath.replace("classpath:", "");
            logger.info("Loading JSON storage file from: {}", path);
            try(InputStream in = getClass().getClassLoader().getResourceAsStream(path)){
                if (in == null) {
                    logger.error("JSON file NOT FOUND: {}", storageDataPath);
                    throw new RuntimeException("JSON file not found in classpath: " + storageDataPath);
                }
                logger.info("JSON storage file loaded successfully.");
                return mapper.readTree(in);
            }
        } catch (Exception e) {
            logger.error("Failed to load JSON file: {}", e.getMessage());
            throw new RuntimeException("Failed to load JSON storage file", e);
        }
    }
}
