package org.example.sampleproblemspringweb.service;

import org.example.sampleproblemspringweb.dto.UserDTO;
import org.example.sampleproblemspringweb.dto.UserResponseDTO;
import org.example.sampleproblemspringweb.exception.EmailAlreadyExistsException;
import org.example.sampleproblemspringweb.exception.UserNotFoundException;
import org.example.sampleproblemspringweb.model.User;
import org.example.sampleproblemspringweb.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.info("UserService initialized");
    }

    public UserResponseDTO createUser(UserDTO userDTO) {
        logger.debug("Creating user with email: {}", userDTO.getEmail());
        
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warn("Attempted to create user with existing email: {}", userDTO.getEmail());
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }

        User user = new User(userDTO.getName(), userDTO.getEmail(), userDTO.getDescription());
        User savedUser = userRepository.save(user);
        logger.info("User created successfully with ID: {} and email: {}", 
            savedUser.getId(), savedUser.getEmail());
        return mapToResponseDTO(savedUser);
    }

    @Transactional(readOnly = true)
    public UserResponseDTO getUserById(Long id) {
        logger.debug("Fetching user with ID: {}", id);
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("User not found with ID: {}", id);
                return new UserNotFoundException(id);
            });
        logger.debug("User found with ID: {} and email: {}", user.getId(), user.getEmail());
        return mapToResponseDTO(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponseDTO> getAllUsers() {
        logger.debug("Fetching all users");
        List<UserResponseDTO> users = userRepository.findAll().stream()
            .map(this::mapToResponseDTO)
            .collect(Collectors.toList());
        logger.info("Retrieved {} users from database", users.size());
        return users;
    }

    public UserResponseDTO updateUser(Long id, UserDTO userDTO) {
        logger.debug("Updating user with ID: {}", id);
        
        User user = userRepository.findById(id)
            .orElseThrow(() -> {
                logger.warn("User not found for update with ID: {}", id);
                return new UserNotFoundException(id);
            });

        // Check if email is being changed and if new email already exists
        if (!user.getEmail().equals(userDTO.getEmail()) && 
            userRepository.existsByEmail(userDTO.getEmail())) {
            logger.warn("Attempted to update user {} with existing email: {}", id, userDTO.getEmail());
            throw new EmailAlreadyExistsException(userDTO.getEmail());
        }

        logger.debug("Updating user ID: {} - Name: {} -> {}, Email: {} -> {}", 
            id, user.getName(), userDTO.getName(), user.getEmail(), userDTO.getEmail());
        
        user.setName(userDTO.getName());
        user.setEmail(userDTO.getEmail());
        user.setDescription(userDTO.getDescription());
        
        User updatedUser = userRepository.save(user);
        logger.info("User updated successfully with ID: {} and email: {}", 
            updatedUser.getId(), updatedUser.getEmail());
        return mapToResponseDTO(updatedUser);
    }

    public void deleteUser(Long id) {
        logger.debug("Deleting user with ID: {}", id);
        
        if (!userRepository.existsById(id)) {
            logger.warn("User not found for deletion with ID: {}", id);
            throw new UserNotFoundException(id);
        }
        
        userRepository.deleteById(id);
        logger.info("User deleted successfully with ID: {}", id);
    }

    private UserResponseDTO mapToResponseDTO(User user) {
        return new UserResponseDTO(
            user.getId(),
            user.getName(),
            user.getEmail(),
            user.getDescription()
        );
    }
}

