package org.example.sampleproblemspringweb.config;

import org.example.sampleproblemspringweb.model.User;
import org.example.sampleproblemspringweb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        logger.info("Starting data initialization...");
        long userCount = userRepository.count();
        logger.info("Current user count in database: {}", userCount);
        
        // Initialize with some sample data
        if (userCount == 0) {
            logger.info("Database is empty, initializing with sample data...");
            
            User user1 = userRepository.save(new User("John Doe", "john.doe@example.com", "Software Developer"));
            logger.info("Created sample user: {} (ID: {})", user1.getEmail(), user1.getId());
            
            User user2 = userRepository.save(new User("Jane Smith", "jane.smith@example.com", "Product Manager"));
            logger.info("Created sample user: {} (ID: {})", user2.getEmail(), user2.getId());
            
            User user3 = userRepository.save(new User("Bob Johnson", "bob.johnson@example.com", "Designer"));
            logger.info("Created sample user: {} (ID: {})", user3.getEmail(), user3.getId());
            
            logger.info("Data initialization completed. Total users: {}", userRepository.count());
        } else {
            logger.info("Database already contains {} users, skipping initialization", userCount);
        }
    }
}

