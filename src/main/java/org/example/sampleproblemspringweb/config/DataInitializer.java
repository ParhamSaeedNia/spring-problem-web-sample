package org.example.sampleproblemspringweb.config;

import org.example.sampleproblemspringweb.model.User;
import org.example.sampleproblemspringweb.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;

    public DataInitializer(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) {
        // Initialize with some sample data
        if (userRepository.count() == 0) {
            userRepository.save(new User("John Doe", "john.doe@example.com", "Software Developer"));
            userRepository.save(new User("Jane Smith", "jane.smith@example.com", "Product Manager"));
            userRepository.save(new User("Bob Johnson", "bob.johnson@example.com", "Designer"));
        }
    }
}

