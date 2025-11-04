package org.example.sampleproblemspringweb.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class LoggingConfig {

    private static final Logger logger = LoggerFactory.getLogger(LoggingConfig.class);

    @EventListener(ApplicationReadyEvent.class)
    public void ensureLogDirectoryExists() {
        try {
            String userDir = System.getProperty("user.dir");
            Path logsDir = Paths.get(userDir, "logs");
            
            if (!Files.exists(logsDir)) {
                Files.createDirectories(logsDir);
                logger.info("Created logs directory at: {}", logsDir.toAbsolutePath());
            } else {
                logger.info("Logs directory already exists at: {}", logsDir.toAbsolutePath());
            }
            
            // Verify write permissions
            File logsFile = new File(logsDir.toFile(), "application.log");
            if (!logsFile.exists()) {
                logsFile.createNewFile();
                logger.info("Created application.log file at: {}", logsFile.getAbsolutePath());
            }
            
            logger.info("Log files will be written to: {}", logsDir.toAbsolutePath());
        } catch (IOException e) {
            logger.error("Failed to create logs directory", e);
        }
    }
}

