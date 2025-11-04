package org.example.sampleproblemspringweb;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SampleProblemSpringWebApplication {

    private static final Logger logger = LoggerFactory.getLogger(SampleProblemSpringWebApplication.class);

    public static void main(String[] args) {
        logger.info("Starting Sample Problem Spring Web Application...");
        logger.info("Application version: 0.0.1-SNAPSHOT");
        logger.info("Java version: {}", System.getProperty("java.version"));
        
        try {
            SpringApplication.run(SampleProblemSpringWebApplication.class, args);
            logger.info("Application started successfully!");
            logger.info("Swagger UI available at: http://localhost:8080/swagger-ui.html");
            logger.info("H2 Console available at: http://localhost:8080/h2-console");
            logger.info("API endpoints available at: http://localhost:8080/api/users");
        } catch (Exception e) {
            logger.error("Failed to start application", e);
            throw e;
        }
    }

}
